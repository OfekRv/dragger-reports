package dragger.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.QuerySource;
import dragger.repositories.QuerySourceRepository;

@RestController
public class QuerySourceController {
	@Autowired
	private QuerySourceRepository sourcesRepository;

	@GetMapping("/sources/getSources")
	public Collection<QuerySource> getSources() {
		return sourcesRepository.findAll();
	}

	@PostMapping("/sources/createSource")
	public QuerySource createSource(@RequestBody QuerySource source) {
		return sourcesRepository.save(source);
	}

	@DeleteMapping("/sources/deleteSource")
	public void deleteSource(@RequestParam QuerySource source) {
		sourcesRepository.delete(source);
	}
}
