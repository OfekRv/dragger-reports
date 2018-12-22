package dragger.bl.suggestions;

import java.util.Collection;

import dragger.entities.QueryColumn;
import dragger.exceptions.DraggerSuggestionException;

public interface ColumnValueSuggestor {
	public Collection<String> suggestValues(QueryColumn column) throws DraggerSuggestionException;
}
