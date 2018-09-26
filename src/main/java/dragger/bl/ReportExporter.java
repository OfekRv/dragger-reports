package dragger.bl;

import java.io.File;
import java.io.IOException;

import dragger.entities.Report;

public interface ReportExporter {
	public File export(Report reportToExport) throws IOException;
}
