package dragger.bl.exporter;

import java.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Named;

import dragger.entities.charts.Chart;
import dragger.entities.charts.ChartExecutionResult;
import dragger.entities.charts.ChartExecutionResultId;
import dragger.entities.charts.ChartResult;
import dragger.exceptions.DraggerExportException;
import dragger.repositories.ChartExecutionResultRepository;

@Named
public class ChartExecutionResultDbExporter implements ChartExecutionResultExporter {
    @Inject
    private ChartExecutionResultRepository repository;

    public void export(ChartResult results, Chart chart) throws DraggerExportException {
        ChartExecutionResultId resultsId = new ChartExecutionResultId(chart, LocalDate.now());
        ChartExecutionResult chartResult = new ChartExecutionResult(resultsId, results);

        // if a chart from this date already exist we override it
        repository.save(chartResult);
    }
}