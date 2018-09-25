package dragger.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.QueryColumn;
import dragger.repositories.QueryColumnRepository;

@RestController
public class QueryColumnController {
	@Autowired
	QueryColumnRepository columnRepository;

	@GetMapping("/columns/{sourceId}/getColumnsBySource")
	public Collection<QueryColumn> getColumnsBySourceId(@PathVariable long sourceId) {
		return columnRepository.findQueryColumnsBySourceId(sourceId);
	}

	@GetMapping("/columns/{queryColumnId}/getConnections")
	public Collection<QueryColumn> getColumnConnections(@PathVariable long queryColumnId) {
		return columnRepository.findConnectionsById(queryColumnId);
	}
}
