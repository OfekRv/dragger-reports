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
import dragger.repositories.QueryColumnRepository;

@RestController
public class QueryColumnController {
	@Autowired
	QueryColumnRepository columnRepository;

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

	@PostMapping("/columns/createColumn")
	public QueryColumn createColumn(@RequestBody QueryColumn column) {
		return columnRepository.save(column);
	}

	@DeleteMapping("/columns/deleteReport")
	public void deleteColumn(@RequestBody QueryColumn column) {
		columnRepository.delete(column);
	}
}
