package dragger.bl.exporter;

import java.util.Collection;

import dragger.entities.charts.Chart;
import dragger.entities.charts.ChartColumnResult;
import dragger.exceptions.DraggerExportException;

public interface ChartQueryExporter {
	public Collection<ChartColumnResult> export(Chart chartQuery) throws DraggerExportException;
}
