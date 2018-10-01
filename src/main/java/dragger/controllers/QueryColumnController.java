package dragger.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.QueryColumn;
import dragger.entities.SourceConnection;
import dragger.repositories.QueryColumnRepository;
import dragger.repositories.SourceConnectionReposiroty;

@RestController
public class QueryColumnController {
	@Autowired
	private QueryColumnRepository columnRepository;

	@Autowired
	private SourceConnectionReposiroty connectionsRepository;

	@GetMapping("/columns/getColumns")
	public Collection<QueryColumn> getColumns() {
		return columnRepository.findAll();
	}

	@GetMapping("/columns/{sourceId}/getColumnsBySource")
	public Collection<QueryColumn> getColumnsBySourceId(@PathVariable long sourceId) {
		return columnRepository.findQueryColumnsBySourceSourceId(sourceId);
	}

	@GetMapping("/columns/{queryColumnId}/getConnections")
	public Collection<QueryColumn> getColumnConnections(@PathVariable long queryColumnId) {
		return columnRepository.findConnectionsByColumnId(queryColumnId);
	}

	@GetMapping("/columns/createConnection")
	public SourceConnection createConnection(@RequestBody SourceConnection connection) {
		return connectionsRepository.save(connection);
	}

	@PostMapping("/columns/createColumn")
	public QueryColumn createColumn(@RequestBody QueryColumn column) {
		return columnRepository.save(column);
	}

	@DeleteMapping("/columns/deleteColumn")
	public void deleteColumn(@RequestBody QueryColumn column) {
		columnRepository.delete(column);
	}
}
