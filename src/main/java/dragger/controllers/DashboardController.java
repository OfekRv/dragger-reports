package dragger.controllers;

import dragger.contracts.ChartToDashboardContract;
import dragger.repositories.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import dragger.entities.Chart;
import dragger.entities.Dashboard;
import dragger.exceptions.DraggerException;

@RestController
public class DashboardController {

	@Autowired
	private ChartRepository chartRepository;

	@DeleteMapping("api/dashboard/removeChart")
	public void removeChartFromDashboard(@RequestBody Dashboard dashboard, @RequestBody Chart chartToRemove)
			throws DraggerException {
		dashboard.getCharts().remove(chartToRemove);
	}
}