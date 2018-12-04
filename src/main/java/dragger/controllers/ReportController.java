package dragger.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import dragger.bl.generator.QueryGenerator;
import dragger.contracts.ReportQueryFilterContract;
import dragger.entities.Filter;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
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
	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private QueryColumnRepository columnRepository;

	@Autowired
	private FilterRepository filterRepository;

	@Autowired
	private QueryGenerator generator;

	@Autowired
	private ReportExporter exporter;

	@PostMapping("api/queries/isQueryLinked")
	public boolean isAllSourcesConnected(@RequestBody Collection<Long> columns) throws DraggerException {
		return generator.isAllSourcesConnected(getSources(getColumnFromIds(columns)));
	}

	@GetMapping("/api/reports/getRawQuery")
	// practically, for debug and stuff
	public String executeReport(@RequestParam long reportId) throws Exception {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (requestedReport.isPresent()) {
			return generator.generate(requestedReport.get().getQuery(), null);
		}

		throw new DraggerException("Report id:" + reportId + " not found");
	}

	@GetMapping("api/reports/generateReport")
	public ResponseEntity<org.springframework.core.io.Resource> generateReport(@RequestParam long reportId)
			throws DraggerException {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (!requestedReport.isPresent()) {
			throw new DraggerControllerReportNotFoundException("Report id:" + reportId + " not found");
		}

		File reportFile = exporter.export(requestedReport.get(), null);
		InputStreamResource resource = createFileResource(reportFile);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportFile.getName() + "\"")
				.contentLength(reportFile.length()).contentType(MediaType.parseMediaType(APPLICATION_OCTET_STREAM))
				.body(resource);
	}

	@PostMapping("api/reports/generateFilteredReport")
	public ResponseEntity<org.springframework.core.io.Resource> generateFilteredReport(@RequestParam long reportId,
			@RequestBody Collection<ReportQueryFilterContract> filters) throws DraggerException {
		Optional<Report> requestedReport = reportRepository.findById(reportId);

		if (!requestedReport.isPresent()) {
			throw new DraggerControllerReportNotFoundException("Report id:" + reportId + " not found");
		}

		File reportFile = exporter.export(requestedReport.get(), createReportFilters(filters));
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

	private Collection<QuerySource> getSources(Collection<QueryColumn> columns) {
		Collection<QuerySource> sources = new ArrayList<>();
		columns.forEach(column -> {
			if (!sources.contains(column.getSource())) {
				sources.add(column.getSource());
			}
		});
		return sources;
	}

	private Collection<QueryColumn> getColumnFromIds(Collection<Long> columnsResources) throws DraggerException {
		Collection<QueryColumn> columns = new ArrayList<>();

		for (Long columnId : columnsResources) {
			columns.add(findColumnById(columnId));
		}
		return columns;
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
}
