package dragger.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.exporter.ReportExporter;
import dragger.bl.generator.QueryGenerator;
import dragger.entities.QuerySource;
import dragger.entities.Report;
import dragger.entities.SourceConnection;
import dragger.exceptions.DraggerControllerException;
import dragger.exceptions.DraggerControllerReportNotFoundException;
import dragger.exceptions.DraggerException;
import dragger.repositories.ReportRepository;

@RestController
public class ReportController {
	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private QueryGenerator generator;

	@Autowired
	private ReportExporter exporter;

	@PostMapping("api/reports/isAllSourcesConnected")
	public boolean isAllSourcesConnected(@RequestBody Collection<QuerySource> sources) {
		return generator.isAllSourcesConnected(sources);
	}

	@GetMapping("/api/reports/getRawQuery")
	// practically, for debug and stuff
	public String executeReport(@RequestParam long reportId) throws Exception {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (requestedReport.isPresent()) {
			return generator.generate(requestedReport.get().getQuery());
		}

		throw new DraggerException("Report id:" + reportId + " not found");
	}

	@GetMapping("api/reports/generateReport")
	public ResponseEntity<Resource> generateReport(@RequestParam long reportId) throws DraggerException {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (!requestedReport.isPresent()) {
			throw new DraggerControllerReportNotFoundException("Report id:" + reportId + " not found");
		}

		File reportFile = exporter.export(requestedReport.get());
		InputStreamResource resource = createFileResource(reportFile);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportFile.getName() + "\"")
				.contentLength(reportFile.length()).contentType(MediaType.parseMediaType(APPLICATION_OCTET_STREAM))
				.body(resource);
	}

	private InputStreamResource createFileResource(File reportFile) throws DraggerControllerException {
		InputStreamResource resource;
		try {
			resource = new InputStreamResource(new FileInputStream(reportFile));
		} catch (IOException e) {
			throw new DraggerControllerException("Could not create file resource", e);
		}
		return resource;
	}
}
