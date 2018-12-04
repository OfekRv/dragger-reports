package dragger.bl.exporter;

import static dragger.utils.ExcelFileUtil.CreateCell;
import static dragger.utils.ExcelFileUtil.createDataCellStyle;
import static dragger.utils.ExcelFileUtil.createFilterCellStyle;
import static dragger.utils.ExcelFileUtil.createHeaderCellStyle;
import static dragger.utils.ExcelFileUtil.createTitleCellStyle;
import static dragger.utils.ExcelFileUtil.saveExcelFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import dragger.bl.executor.QueryExecutor;
import dragger.bl.generator.QueryGenerator;
import dragger.entities.Report;
import dragger.entities.ReportQueryFilter;
import dragger.exceptions.DraggerException;
import dragger.exceptions.DraggerExportException;

@Named
public class ExcelReportExporter implements ReportExporter {
	private static final char UNDER_LINE = '_';
	private static final char SPACE = ' ';
	private static final String SUFFIX = ".xlsx";
	private static final String PARENT_DIRECTORIES = "reports/";
	private static final String EMPTY = "";
	private static final int TITLE_ROW = 0;
	private static final int HEADER_ROW = 3;
	private static final int FILTERS_ROW = 3;
	private static final int AFTER_FILTERS_INCREMENT = 1;
	private static final int FIRST_COLUMN_INDEX = 0;
	private static final int COLUMN_NAME_COLUMN_INDEX = 2;
	private static final int FILTER_NAME_COLUMN_INDEX = 1;
	private static final int VALUE_COLUMN_INDEX = 0;

	@Inject
	private QueryGenerator generator;
	@Inject
	private QueryExecutor executor;

	@Override
	public File export(Report reportToExport, Collection<ReportQueryFilter> filters) throws DraggerExportException {
		String reportName = generateReportName(reportToExport);
		String reportFilePath = PARENT_DIRECTORIES + reportName;
		SqlRowSet results;
		try {
			results = executor.executeQuery(generator.generate(reportToExport.getQuery(), filters));
		} catch (DraggerException e) {
			throw new DraggerExportException("Could not generate the query", e);
		}
		SqlRowSetMetaData resultsMetaData = results.getMetaData();

		try (Workbook workbook = new XSSFWorkbook();) {
			Sheet sheet = workbook.createSheet(reportName);
			createTitle(reportToExport, workbook, sheet);

			int currentExcelRow;

			if (filters != null) {
				currentExcelRow = createFiltersTable(filters, workbook, sheet);
				currentExcelRow += AFTER_FILTERS_INCREMENT;
			} else {
				currentExcelRow = HEADER_ROW;
			}

			currentExcelRow = createHeaderRowFromMetadata(resultsMetaData, workbook, sheet, currentExcelRow);
			int autoExcelFilterRow = currentExcelRow - 1;
			int excelLastRowIndex = createDataTableFromResultset(results, resultsMetaData, workbook, sheet,
					currentExcelRow);
			setTableAutoFilter(resultsMetaData, sheet, excelLastRowIndex, autoExcelFilterRow);
			autoSizeColumns(resultsMetaData, sheet);
			saveExcelFile(reportFilePath, workbook);

		} catch (IOException e) {
			throw new DraggerExportException("Could not create export file", e);
		}

		return new File(reportFilePath);
	}

	private String generateReportName(Report reportToExport) {
		return reportToExport.getName().replace(SPACE, UNDER_LINE) + UNDER_LINE + LocalDate.now() + SUFFIX;
	}

	private void autoSizeColumns(SqlRowSetMetaData resultsMetaData, Sheet sheet) {
		for (int i = FIRST_COLUMN_INDEX; i < resultsMetaData.getColumnCount(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void setTableAutoFilter(SqlRowSetMetaData resultsMetaData, Sheet sheet, int lastDataRowIndex,
			int autoExcelFilterRow) {
		sheet.setAutoFilter(new CellRangeAddress(autoExcelFilterRow, lastDataRowIndex, FIRST_COLUMN_INDEX,
				resultsMetaData.getColumnCount() - 1));
	}

	private int createDataTableFromResultset(SqlRowSet results, SqlRowSetMetaData resultsMetaData, Workbook workbook,
			Sheet sheet, int currentExcelRow) {
		CellStyle DataStyle = createDataCellStyle(workbook);

		while (results.next()) {
			Row row = sheet.createRow(currentExcelRow);

			for (int i = FIRST_COLUMN_INDEX; i < resultsMetaData.getColumnCount(); i++) {
				Object data = results.getObject(resultsMetaData.getColumnNames()[i]);

				if (data == null) {
					data = EMPTY;
				}

				CreateCell(data.toString(), DataStyle, row, i);
			}

			currentExcelRow++;
		}
		return currentExcelRow;
	}

	private void createTitle(Report report, Workbook workbook, Sheet sheet) {
		Row titleRow = sheet.createRow(TITLE_ROW);
		CellStyle titleStyle = createTitleCellStyle(workbook);
		CreateCell(report.getName(), titleStyle, titleRow, FIRST_COLUMN_INDEX);
		CreateCell(LocalDate.now().toString(), titleStyle, sheet.createRow(TITLE_ROW + 1), FIRST_COLUMN_INDEX);
	}

	private int createFiltersTable(Collection<ReportQueryFilter> filters, Workbook workbook, Sheet sheet) {
		Row filterRow;
		CellStyle FilterStyle = createFilterCellStyle(workbook);
		int rowIndex = FILTERS_ROW;

		for (ReportQueryFilter filter : filters) {
			filterRow = sheet.createRow(rowIndex);
			CreateCell(filter.getValue(), FilterStyle, filterRow, VALUE_COLUMN_INDEX);
			CreateCell(filter.getFilter().getName(), FilterStyle, filterRow, FILTER_NAME_COLUMN_INDEX);
			CreateCell(filter.getColumn().getName(), FilterStyle, filterRow, COLUMN_NAME_COLUMN_INDEX);
			rowIndex++;
		}

		return rowIndex;
	}

	private int createHeaderRowFromMetadata(SqlRowSetMetaData resultsMetaData, Workbook workbook, Sheet sheet,
			int currentExcelRow) {
		Row headerRow = sheet.createRow(currentExcelRow);
		CellStyle headerStyle = createHeaderCellStyle(workbook);

		for (int i = FIRST_COLUMN_INDEX; i < resultsMetaData.getColumnCount(); i++) {
			CreateCell(resultsMetaData.getColumnNames()[i], headerStyle, headerRow, i);
		}

		currentExcelRow++;
		return currentExcelRow;
	}
}