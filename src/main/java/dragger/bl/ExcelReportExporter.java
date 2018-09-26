package dragger.bl;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;

import dragger.entities.Report;

@Named
public class ExcelReportExporter implements ReportExporter {

	@Override
	public File export(Report reportToExport) throws IOException {
		// TODO: file stub
		new File("file1.xls").createNewFile();
		return new File("file1.xls");
	}

}
