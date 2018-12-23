package dragger.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.exporter.ReportExporter;
import dragger.contracts.ReportQueryFilterContract;
import dragger.entities.Filter;
import dragger.entities.QueryColumn;
import dragger.entities.Report;
import dragger.entities.ReportQueryFilter;
import dragger.exceptions.DraggerControllerException;
import dragger.exceptions.DraggerControllerReportNotFoundException;
import dragger.exceptions.DraggerException;
import dragger.repositories.FilterRepository;
import dragger.repositories.QueryColumnRepository;
import dragger.repositories.ReportRepository;

@RestController
public class ReportController {
	private static final String UTF_8 = "UTF-8";
	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private QueryColumnRepository columnRepository;

	@Autowired
	private FilterRepository filterRepository;

	@Autowired
	private ReportExporter exporter;

	@GetMapping("api/reports/generateReport")
	public ResponseEntity<org.springframework.core.io.Resource> generateReport(@RequestParam long reportId,
			@RequestParam boolean showDuplicates) throws DraggerException {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (!requestedReport.isPresent()) {
			throw new DraggerControllerReportNotFoundException("Report id:" + reportId + " not found");
		}

		File reportFile = exporter.export(requestedReport.get(), null, showDuplicates);
		InputStreamResource resource = createFileResource(reportFile);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + getReportFileName(reportFile) + "\"")
				.contentLength(reportFile.length()).contentType(MediaType.parseMediaType(APPLICATION_OCTET_STREAM))
				.body(resource);
	}

	@PostMapping("api/reports/generateFilteredReport")
	public ResponseEntity<org.springframework.core.io.Resource> generateFilteredReport(@RequestParam long reportId,
			@RequestParam boolean showDuplicates, @RequestBody Collection<ReportQueryFilterContract> filters)
			throws DraggerException {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (!requestedReport.isPresent()) {
			throw new DraggerControllerReportNotFoundException("Report id:" + reportId + " not found");
		}

		File reportFile = exporter.export(requestedReport.get(), createReportFilters(filters), showDuplicates);
		InputStreamResource resource = createFileResource(reportFile);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + getReportFileName(reportFile) + "\"")
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

	private Collection<ReportQueryFilter> createReportFilters(Collection<ReportQueryFilterContract> contractedFilters)
			throws DraggerException {
		Collection<ReportQueryFilter> filters = new ArrayList<>();

		for (ReportQueryFilterContract contractedFilter : contractedFilters) {
			filters.add(createReportFilter(contractedFilter));
		}

		return filters;
	}

	private ReportQueryFilter createReportFilter(ReportQueryFilterContract contractedFilter) throws DraggerException {
		ReportQueryFilter filter = new ReportQueryFilter(findFilterById(contractedFilter.getFilterId()),
				findColumnById(contractedFilter.getColumnId()), contractedFilter.getValue());

		return filter;
	}

	private QueryColumn findColumnById(long columnId) throws DraggerException {
		Optional<QueryColumn> requestedColumn = columnRepository.findById(columnId);

		if (requestedColumn.isPresent()) {
			return requestedColumn.get();
		}

		throw new DraggerException("Column id:" + columnId + " not found");
	}

	private Filter findFilterById(long filterId) throws DraggerException {
		Optional<Filter> requestedFilter = filterRepository.findById(filterId);

		if (requestedFilter.isPresent()) {
			return requestedFilter.get();
		}

		throw new DraggerException("Filter id:" + filterId + " not found");
	}

	private String getReportFileName(File reportFile) throws DraggerControllerException {
		try {
			return URLEncoder.encode(reportFile.getName(), UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new DraggerControllerException("could not parse report name", e);
		}
	}
}
