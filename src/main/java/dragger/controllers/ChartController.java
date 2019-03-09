package dragger.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.exporter.ChartQueryExporter;
import dragger.entities.Chart;
import dragger.exceptions.DraggerControllerReportNotFoundException;
import dragger.exceptions.DraggerException;
import dragger.repositories.ChartRepository;

@RestController
public class ChartController {
	@Autowired
	private ChartRepository chartRepository;

	@Autowired
	private ChartQueryExporter exporter;

	@PostMapping("api/charts/executeCountChartQuery")
	public void generateFilteredReport(@RequestParam long chartId) throws DraggerException {
		Optional<Chart> requestedChart = chartRepository.findById(chartId);

		if (!requestedChart.isPresent()) {
			throw new DraggerControllerReportNotFoundException("Chart id:" + chartId + " not found");
		}

		exporter.export(requestedChart.get());
	}
}