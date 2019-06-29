package dragger.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.generator.QueryGenerator;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.exceptions.DraggerException;
import dragger.repositories.QueryColumnRepository;

@RestController
public class QueryController {
	@Autowired
	private QueryColumnRepository columnRepository;

	@Autowired
	private QueryGenerator generator;

	@PostMapping("api/queries/isQueryLinked")
	public boolean isAllSourcesConnected(@RequestBody Collection<Long> columns) throws DraggerException {
		return generator.isAllSourcesConnected(getColumnFromIds(columns).stream().map(QueryColumn::getSource).collect(Collectors.toList()));
	}

	private Collection<QueryColumn> getColumnFromIds(Collection<Long> columnsResources) throws DraggerException {
		Collection<QueryColumn> columns = new ArrayList<>();

		for (Long columnId : columnsResources) {
			Optional<QueryColumn> requestedColumn = columnRepository.findById(columnId);

			if (!requestedColumn.isPresent()) {
				throw new DraggerException("Column id:" + columnId + " not found");
			}

			columns.add(requestedColumn.get());
		}
		return columns;
	}
}
