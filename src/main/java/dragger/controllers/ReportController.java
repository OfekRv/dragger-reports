package dragger.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.QueryGenerator;
import dragger.bl.ReportExporter;
import dragger.entities.Report;
import dragger.repositories.ReportRepository;

@RestController
public class ReportController {
	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private QueryGenerator generator;

	@Autowired
	private ReportExporter exporter;

	@GetMapping("/reports/getRawQuery")
	// practically, for debug and stuff
	public String executeReport(@RequestParam long reportId) throws Exception {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (requestedReport.isPresent()) {
			return generator.generate(requestedReport.get().getQuery());
		}

		throw new Exception("");
	}

	@GetMapping("/reports/generateReport")
	// TODO: for now its just returns the query, later it will produce a report
	// and download
	public ResponseEntity<Resource> generateReport(@RequestParam long reportId) throws Exception {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (!requestedReport.isPresent()) {
			throw new Exception("");
		}
		File reportFile = exporter.export(requestedReport.get());
		InputStreamResource resource = new InputStreamResource(new FileInputStream(reportFile));

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportFile.getName() + "\"")
				.contentLength(reportFile.length()).contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(resource);
	}

	@GetMapping("/reports/getReports")
	public Collection<Report> getReports() {
		return reportRepository.findAll();
	}

	@GetMapping("/reports/getReport/{reportId}")
	public Optional<Report> getReport(@PathVariable long reportId) {
		return reportRepository.findById(reportId);
	}

	@PostMapping("/reports/createReport")
	public Report createReport(@RequestParam Report report) {
		return reportRepository.save(report);
	}

	@DeleteMapping("/reports/deleteReport")
	public void deleteReport(@RequestParam Report report) {
		reportRepository.delete(report);
	}
}
