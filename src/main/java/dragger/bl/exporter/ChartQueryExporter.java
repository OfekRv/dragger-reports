package dragger.bl.exporter;

import java.util.Collection;

import dragger.contracts.ChartResult;
import dragger.entities.Chart;
import dragger.exceptions.DraggerExportException;

public interface ChartQueryExporter {
	public Collection<ChartResult> export(Chart chartQuery) throws DraggerExportException;
}
