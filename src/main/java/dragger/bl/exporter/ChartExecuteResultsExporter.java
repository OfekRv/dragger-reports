package dragger.bl.exporter;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import dragger.bl.executor.QueryExecutor;
import dragger.bl.generator.QueryGenerator;
import dragger.contracts.ChartResult;
import dragger.entities.Chart;
import dragger.exceptions.DraggerException;
import dragger.exceptions.DraggerExportException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
public class ChartExecuteResultsExporter implements ChartQueryExporter {
	private static final String COUNT = "COUNT(*), ";
	private static final int COUNT_COLUMN_INDEX = 0;
	private static final int DATA_COLUMN_INDEX = 1;
	private static final String EMPTY = "";
	private static final String SELECT_IN_QUERY = "SELECT ";
	private static final boolean SHOW_DUPLICATES = true;

	@Inject
	private QueryGenerator generator;
	@Inject
	private QueryExecutor executor;

	public Collection<ChartResult> export(Chart chartQuery) throws DraggerExportException {
		SqlRowSet results;

		log.info("executing chart query");
		try {
			StringBuilder rawQuery = new StringBuilder(
					generator.generate(chartQuery.getQuery(), null, SHOW_DUPLICATES));
			rawQuery.insert(rawQuery.indexOf(SELECT_IN_QUERY) + SELECT_IN_QUERY.length(), COUNT);
			results = executor.executeQuery(rawQuery.toString());
		} catch (DraggerException e) {
			log.error("execution of chart query of failed");
			throw new DraggerExportException("Could not generate the query", e);
		}

		Collection<ChartResult> chartResults = new ArrayList<>();
		while (results.next()) {

			long count = results.getLong(COUNT_COLUMN_INDEX);
			Object label = results.getObject(DATA_COLUMN_INDEX);

			if (label == null) {
				label = EMPTY;
			}

			chartResults.add(new ChartResult(label.toString(), count));
		}

		log.info("chart query executed successfully");
		return chartResults;
	}
}