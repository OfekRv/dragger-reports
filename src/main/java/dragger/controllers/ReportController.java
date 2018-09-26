package dragger.controllers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.QueryGenerator;
import dragger.entities.Report;
import dragger.repositories.ReportRepository;

@RestController
public class ReportController {
	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private QueryGenerator generator;

	@PostMapping("/reports/executeReport")
	// TODO: for now its just returns the query, later it will produce a report
	// and download
	public String executeReport(@RequestBody Report report) {
		return generator.generate(report.getQuery());
	}

	@GetMapping("/reports/executeReport")
	// TODO: for now its just returns the query, later it will produce a report
	// and download
	public String executeReport(@RequestParam long reportId) throws Exception {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (requestedReport.isPresent()) {
			return generator.generate(requestedReport.get().getQuery());
		}

		throw new Exception("");
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
