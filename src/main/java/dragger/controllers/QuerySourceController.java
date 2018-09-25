package dragger.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.QuerySource;
import dragger.repositories.QuerySourceRepository;

@RestController
public class QuerySourceController {
	@Autowired
	QuerySourceRepository sourcesRepository;

	@GetMapping("/columns/getSources")
	public Collection<QuerySource> getSources() {
		return sourcesRepository.findAll();
	}

	@PostMapping("/columns/createReport")
	public QuerySource createSource(@PathVariable QuerySource source) {
		return sourcesRepository.save(source);
	}

	@DeleteMapping("/columns/deleteReport")
	public void deleteSource(@PathVariable QuerySource source) {
		sourcesRepository.delete(source);
	}
}
