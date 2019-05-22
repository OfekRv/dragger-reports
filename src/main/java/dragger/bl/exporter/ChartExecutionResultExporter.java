package dragger.bl.exporter;

import dragger.entities.Chart;
import dragger.entities.charts.ChartResult;
import dragger.exceptions.DraggerExportException;

public interface ChartExecutionResultExporter {
	public void export(ChartResult results, Chart chart) throws DraggerExportException;
}
