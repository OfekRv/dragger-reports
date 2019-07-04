package dragger.bl.scheduled;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;

import dragger.bl.exporter.ChartQueryExporter;
import dragger.entities.Dashboard;
import dragger.entities.charts.Chart;
import dragger.exceptions.DraggerExportException;
import dragger.repositories.DashboardRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Named
@Slf4j
public class ChartScheduledExecutor {
    @Inject
    DashboardRepository dashboardRepository;
    @Inject
    ChartQueryExporter exporter;

    @Scheduled(cron = "${cron.expression}")
    @Transactional
    public void executeCharts() {
        log.info("started executing all dashbaords charts");
        dashboardRepository.findAll().forEach(dashboard -> executeDashboardCharts(dashboard));
        log.info("finished executing all dashbaords charts");
    }

    private void executeDashboardCharts(Dashboard dashboard) {
        log.info("executing dashboard " + dashboard.getId());
        dashboard.getCharts().forEach(chart -> executeChart(chart));
    }

    private void executeChart(Chart chart) {
        try {
            exporter.export(chart, chart.getFilters());
        } catch (DraggerExportException e) {
            // next please!
        }
    }
}