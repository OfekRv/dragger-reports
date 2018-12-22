package dragger.controllers;

import static java.util.Arrays.asList;

import java.sql.JDBCType;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.suggestions.ColumnValueSuggestor;
import dragger.entities.QueryColumn;
import dragger.exceptions.DraggerException;
import dragger.exceptions.DraggerSuggestionException;
import dragger.repositories.QueryColumnRepository;

@RestController
public class ColumnController {
	@Inject
	ColumnValueSuggestor suggestor;

	@Inject
	QueryColumnRepository columnRepository;

	@GetMapping("api/columns/availableDataTypes")
	public Collection<JDBCType> getAvailableDataTypes() {
		return asList(JDBCType.NUMERIC, JDBCType.VARCHAR, JDBCType.DATE, JDBCType.BOOLEAN);
	}

	@GetMapping("api/columns/suggestValues")
	public Collection<String> suggestValues(long columnId) throws DraggerException {
		return suggestor.suggestValues(findColumnById(columnId));
	}

	private QueryColumn findColumnById(long columnId) throws DraggerException {
		Optional<QueryColumn> requestedColumn = columnRepository.findById(columnId);

		if (requestedColumn.isPresent()) {
			return requestedColumn.get();
		}

		throw new DraggerException("Column id:" + columnId + " not found");
	}
}
