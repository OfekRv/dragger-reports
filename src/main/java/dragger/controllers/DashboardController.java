package dragger.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.Chart;
import dragger.entities.Dashboard;
import dragger.exceptions.DraggerException;

@RestController
public class DashboardController {
	@DeleteMapping("api/dashboard/removeChart")
	public void removeChartFromDashboard(@RequestBody Dashboard dashboard, @RequestBody Chart chartToRemove)
			throws DraggerException {
		dashboard.getCharts().remove(chartToRemove);
	}

	@GetMapping("api/dashboard/addChart")
	public void generateFilteredReport(@RequestParam Dashboard dashboard, @RequestParam Chart chartToRemove)
			throws DraggerException {
		dashboard.getCharts().add(chartToRemove);
	}
}