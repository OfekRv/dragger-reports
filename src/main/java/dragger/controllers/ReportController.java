package dragger.controllers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.Report;
import dragger.repositories.ReportRepository;

@RestController
public class ReportController {
	@Autowired
	private ReportRepository reportRepository;

	@GetMapping("/reports")
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
