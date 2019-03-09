package dragger.bl.exporter;

import java.util.Collection;

import dragger.contracts.ChartResult;
import dragger.entities.Query;
import dragger.exceptions.DraggerExportException;

public interface ChartQueryExporter {
	public Collection<ChartResult> export(Query chartQuery) throws DraggerExportException;
}
