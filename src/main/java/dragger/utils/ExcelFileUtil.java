package dragger.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelFileUtil {
	public static void saveExcelFile(String fileName, Workbook workbook) throws IOException, FileNotFoundException {
		Files.createDirectories(Paths.get(fileName).getParent());

		try (FileOutputStream fileOut = new FileOutputStream(fileName);) {
			workbook.write(fileOut);
		}
	}

	public static void CreateCell(String data, CellStyle DataStyle, Row row, int cellIndex) {
		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(data);
		cell.setCellStyle(DataStyle);
	}

	public static CellStyle createTitleCellStyle(Workbook workbook) {
		return createCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex(), FillPatternType.NO_FILL);
	}

	public static CellStyle createFilterCellStyle(Workbook workbook) {
		return createCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex(), FillPatternType.NO_FILL);
	}

	public static CellStyle createHeaderCellStyle(Workbook workbook) {
		return createCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex(),
				FillPatternType.SOLID_FOREGROUND);
	}

	public static CellStyle createDataCellStyle(Workbook workbook) {
		return createCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getIndex(),
				FillPatternType.SOLID_FOREGROUND);
	}

	public static CellStyle createCellStyle(Workbook workbook, short foregroundColor, FillPatternType pattern) {
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
