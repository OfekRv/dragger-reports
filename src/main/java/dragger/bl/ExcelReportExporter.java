package dragger.bl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import dragger.entities.Report;

@Named
public class ExcelReportExporter implements ReportExporter {
	private static final String SUFFIX = ".xls";
	private static final int HEADER_ROW = 1;
	private static final int RESULTS_FIRST_ROW = HEADER_ROW + 1;
	private static final int FIRST_COLUMN_INDEX = 0;

	@Inject
	QueryGenerator generator;
	@Inject
	QueryExecutor executor;

	@Override
	public File export(Report reportToExport) throws IOException {
		String reportName = reportToExport.getName() + LocalDate.now() + SUFFIX;
		SqlRowSet results = executor.executeQuery(generator.generate(reportToExport.getQuery()));
		SqlRowSetMetaData resultsMetaData = results.getMetaData();

		try (Workbook workbook = new HSSFWorkbook();) {
			Sheet sheet = workbook.createSheet(reportName);
			createHeaderRowFromMetadata(resultsMetaData, workbook, sheet);
			int excelRowIndex = createDataTableFromResultset(results, resultsMetaData, workbook, sheet);
			setTableAutoFilter(resultsMetaData, sheet, excelRowIndex);
			saveExcelFile(reportName, workbook);
		}

		return new File(reportName);
	}

	private void saveExcelFile(String reportName, Workbook workbook) throws IOException, FileNotFoundException {
		try (FileOutputStream fileOut = new FileOutputStream(reportName);) {
			workbook.write(fileOut);
		}
	}

	private void setTableAutoFilter(SqlRowSetMetaData resultsMetaData, Sheet sheet, int excelRowIndex) {
		sheet.setAutoFilter(new CellRangeAddress(HEADER_ROW, excelRowIndex, FIRST_COLUMN_INDEX,
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

	private void createHeaderRowFromMetadata(SqlRowSetMetaData resultsMetaData, Workbook workbook, Sheet sheet) {
		Row headerRow = sheet.createRow(HEADER_ROW);
		CellStyle headerStyle = createHeaderCellStyle(workbook);

		for (int i = FIRST_COLUMN_INDEX; i < resultsMetaData.getColumnCount(); i++) {
			CreateCell(resultsMetaData.getColumnNames()[i], headerStyle, headerRow, i);
		}
	}

	private void CreateCell(String data, CellStyle DataStyle, Row row, int cellIndex) {
		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(data);
		cell.setCellStyle(DataStyle);
	}

	private CellStyle createHeaderCellStyle(Workbook workbook) {
		return createCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex(),
				FillPatternType.SOLID_FOREGROUND);
	}

	private CellStyle createDataCellStyle(Workbook workbook) {
		return createCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getIndex(),
				FillPatternType.SOLID_FOREGROUND);
	}

	private CellStyle createCellStyle(Workbook workbook, short foregroundColor, FillPatternType pattern) {
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(foregroundColor);
		style.setFillPattern(pattern);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		return style;
	}
}