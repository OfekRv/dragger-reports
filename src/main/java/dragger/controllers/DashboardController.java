package dragger.controllers;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.Chart;
import dragger.entities.Dashboard;
import dragger.exceptions.DraggerException;
import dragger.repositories.ChartRepository;
import dragger.repositories.DashboardRepository;

@RestController
public class DashboardController {
	@Autowired
	private ChartRepository chartRepository;
	@Autowired
	private DashboardRepository dashboardRepository;

	@Transactional
	@DeleteMapping("api/dashboard/{dashboardId}/removeChart/{chartId}")
	public void removeChartFromDashboard(@PathVariable("dashboardId") long dashboardId,
			@PathVariable("chartId") long chartId) throws DraggerException {
		Dashboard dashboard = findDashboardById(dashboardId);
		Chart chartToRemove = findChartById(chartId);
		dashboard.getCharts().remove(chartToRemove);
	}

	@Transactional
	@PutMapping("api/dashboard/{dashboardId}/addChart/{chartId}")
	public void addChartToDashboard(@PathVariable("dashboardId") long dashboardId,
			@PathVariable("chartId") long chartId) throws DraggerException {
		Dashboard dashboard = findDashboardById(dashboardId);
		Chart chartToAdd = findChartById(chartId);
		dashboard.getCharts().add(chartToAdd);
	}

	private Dashboard findDashboardById(long dashboardId) throws DraggerException {
		Optional<Dashboard> requestedDashboard = dashboardRepository.findById(dashboardId);

		if (requestedDashboard.isPresent()) {
			return requestedDashboard.get();
		}

		throw new DraggerException("Dashboard id:" + dashboardId + " not found");
	}

	private Chart findChartById(long chartId) throws DraggerException {
		Optional<Chart> requestedCHart = chartRepository.findById(chartId);

		if (requestedCHart.isPresent()) {
			return requestedCHart.get();
		}

		throw new DraggerException("Chart id:" + chartId + " not found");
	}
}
