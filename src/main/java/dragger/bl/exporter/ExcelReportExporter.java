package dragger.bl.exporter;

import static dragger.utils.ExcelFileUtil.CreateCell;
import static dragger.utils.ExcelFileUtil.createDataCellStyle;
import static dragger.utils.ExcelFileUtil.createHeaderCellStyle;
import static dragger.utils.ExcelFileUtil.createTitleCellStyle;
import static dragger.utils.ExcelFileUtil.saveExcelFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

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
import dragger.exceptions.DraggerExportException;

@Named
public class ExcelReportExporter implements ReportExporter {
	private static final char UNDER_LINE = '_';
	private static final char SPACE = ' ';
	private static final String SUFFIX = ".xlsx";
	private static final String PARENT_DIRECTORIES = "/reports/";
	private static final int TITLE_ROW = 0;
	private static final int HEADER_ROW = 3;
	private static final int RESULTS_FIRST_ROW = HEADER_ROW + 1;
	private static final int FIRST_COLUMN_INDEX = 0;

	@Inject
	private QueryGenerator generator;
	@Inject
	private QueryExecutor executor;

	@Override
	public File export(Report reportToExport) throws DraggerExportException {
		String reportName = generateReportName(reportToExport);
		String reportFilePath = PARENT_DIRECTORIES + reportName;
		SqlRowSet results = executor.executeQuery(generator.generate(reportToExport.getQuery()));
		SqlRowSetMetaData resultsMetaData = results.getMetaData();

		try (Workbook workbook = new XSSFWorkbook();) {
			Sheet sheet = workbook.createSheet(reportName);
			createTitle(reportToExport, workbook, sheet);
			createHeaderRowFromMetadata(resultsMetaData, workbook, sheet);
			int excelRowIndex = createDataTableFromResultset(results, resultsMetaData, workbook, sheet);
			setTableAutoFilter(resultsMetaData, sheet, excelRowIndex);
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

	private void setTableAutoFilter(SqlRowSetMetaData resultsMetaData, Sheet sheet, int lastDataRowIndex) {
		sheet.setAutoFilter(new CellRangeAddress(HEADER_ROW, lastDataRowIndex, FIRST_COLUMN_INDEX,
				resultsMetaData.getColumnCount() - 1));
	}

	private int createDataTableFromResultset(SqlRowSet results, SqlRowSetMetaData resultsMetaData, Workbook workbook,
			Sheet sheet) {
		int excelRowIndex = RESULTS_FIRST_ROW;
		CellStyle DataStyle = createDataCellStyle(workbook);

		while (results.next()) {
			Row row = sheet.createRow(excelRowIndex);

			for (int i = FIRST_COLUMN_INDEX; i < resultsMetaData.getColumnCount(); i++) {
				CreateCell(results.getObject(resultsMetaData.getColumnNames()[i]).toString(), DataStyle, row, i);
			}

			excelRowIndex++;
		}
		return excelRowIndex;
	}

	private void createTitle(Report report, Workbook workbook, Sheet sheet) {
		Row titleRow = sheet.createRow(TITLE_ROW);
		CellStyle titleStyle = createTitleCellStyle(workbook);
		CreateCell(report.getName(), titleStyle, titleRow, FIRST_COLUMN_INDEX);
		CreateCell(LocalDate.now().toString(), titleStyle, sheet.createRow(TITLE_ROW + 1), FIRST_COLUMN_INDEX);
	}

	private void createHeaderRowFromMetadata(SqlRowSetMetaData resultsMetaData, Workbook workbook, Sheet sheet) {
		Row headerRow = sheet.createRow(HEADER_ROW);
		CellStyle headerStyle = createHeaderCellStyle(workbook);

		for (int i = FIRST_COLUMN_INDEX; i < resultsMetaData.getColumnCount(); i++) {
			CreateCell(resultsMetaData.getColumnNames()[i], headerStyle, headerRow, i);
		}
	}
}