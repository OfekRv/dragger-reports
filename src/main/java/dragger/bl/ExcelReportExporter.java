package dragger.bl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import dragger.entities.Report;

@Named
public class ExcelReportExporter implements ReportExporter {
	private static final String SUFFIX = ".xls";
	private static final int HEADER_ROW = 1;

	@Inject
	QueryGenerator generator;
	@Inject
	QueryExecutor executor;

	@Override
	public File export(Report reportToExport) throws IOException {
		String reportName = reportToExport.getName() + LocalDate.now() + SUFFIX;
		SqlRowSet results = executor.executeQuery(generator.generate(reportToExport.getQuery()));
		SqlRowSetMetaData resultsMetaData = results.getMetaData();

		Workbook workbook = new HSSFWorkbook();

		Sheet sheet = workbook.createSheet(reportName);

		Row headerRow = sheet.createRow(HEADER_ROW);

		for (int i = 0; i < resultsMetaData.getColumnCount(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(resultsMetaData.getColumnNames()[i]);
		}

		for (int rowIndex = 1; rowIndex <= resultsMetaData.getColumnCount(); rowIndex++) {
			Row row = sheet.createRow(rowIndex);

			for (int i = 0; i < resultsMetaData.getColumnCount(); i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(resultsMetaData.getColumnNames()[i]);
				// cell.setCellType(CellType.(resultsMetaData.getColumnType(i)));
			}
		}

		//new File(reportName).createNewFile();

		try (FileOutputStream fileOut = new FileOutputStream(reportName);) {
			workbook.write(fileOut);
		}

		return new File(reportName);
	}

}
