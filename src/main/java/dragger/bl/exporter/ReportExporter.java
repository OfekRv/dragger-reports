package dragger.bl.exporter;

import java.io.File;
import java.util.Collection;

import dragger.entities.Report;
import dragger.entities.ReportQueryFilter;
import dragger.exceptions.DraggerExportException;

public interface ReportExporter {
	public File export(Report reportToExport, Collection<ReportQueryFilter> filters, boolean showDuplicates)
			throws DraggerExportException;
}
