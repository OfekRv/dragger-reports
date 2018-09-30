package dragger.bl.exporter;

import java.io.File;

import dragger.entities.Report;
import dragger.exceptions.DraggerExportException;

public interface ReportExporter {
	public File export(Report reportToExport) throws DraggerExportException;
}
