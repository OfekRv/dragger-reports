package dragger.bl.suggestions;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import dragger.bl.executor.QueryExecutor;
import dragger.bl.generator.QueryGenerator;
import dragger.entities.Query;
import dragger.entities.QueryColumn;
import dragger.exceptions.DraggerException;
import dragger.exceptions.DraggerExecuteException;
import dragger.exceptions.DraggerSuggestionException;

@Named
public class ColumnValueRelationalSuggestor implements ColumnValueSuggestor {
	@Inject
	private QueryGenerator generator;
	@Inject
	private QueryExecutor executor;

	public Collection<String> suggestValues(QueryColumn column) throws DraggerSuggestionException {
		String suggestionQuery = null;
		try {
			suggestionQuery = generator.generate(new Query(asList(column), null, null), null, false);
		} catch (DraggerException e) {
			throw new DraggerSuggestionException("Could not generate the suggestion query", e);
		}

		SqlRowSet sqlResults = null;
		Collection<String> results = new ArrayList<>();

		try {
			sqlResults = executor.executeQuery(suggestionQuery);
		} catch (DraggerExecuteException e) {
			throw new DraggerSuggestionException("Could not execute the suggestion query", e);
		}

		while (sqlResults.next()) {
			results.add(sqlResults.getObject(column.getName()).toString());
		}

		return results;
	}
}
