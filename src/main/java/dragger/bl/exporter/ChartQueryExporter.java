package dragger.bl.exporter;

import dragger.contracts.ChartResult;
import dragger.entities.ChartQueryFilter;
import dragger.entities.charts.Chart;
import dragger.entities.charts.ChartColumnResult;
import dragger.exceptions.DraggerExportException;

import java.util.Collection;

public interface ChartQueryExporter {
	public Collection<ChartColumnResult> export(Chart chartQuery, Collection<ChartQueryFilter> filters)
			throws DraggerExportException;
}
