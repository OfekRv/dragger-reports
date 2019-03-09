package dragger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.exporter.ChartQueryExporter;
import dragger.entities.Query;
import dragger.exceptions.DraggerException;

@RestController
public class ChartsController {
	@Autowired
	private ChartQueryExporter exporter;

	@PostMapping("api/charts/executeCountChartQuery")
	public void generateFilteredReport(@RequestBody Query chartQuery) throws DraggerException {
		exporter.export(chartQuery);
	}
}
