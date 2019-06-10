package dragger.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import dragger.bl.generator.QueryGenerator;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.repositories.QueryColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dragger.bl.exporter.ChartQueryExporter;
import dragger.contracts.ChartResult;
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

    @Autowired
    private QueryColumnRepository columnRepository;

    @Autowired
    private QueryGenerator generator;

    @GetMapping("api/charts/executeCountChartQuery")
    public Collection<ChartResult> generateFilteredReport(@RequestParam long chartId) throws DraggerException {
        Optional<Chart> requestedChart = chartRepository.findById(chartId);

        if (!requestedChart.isPresent()) {
            throw new DraggerControllerReportNotFoundException("Chart id:" + chartId + " not found");
        }

        return exporter.export(requestedChart.get(), requestedChart.get().getFilters());
    }

    @GetMapping("api/charts/filterColumnsSuggestion")
    public Collection<QueryColumn> findFilterColumnsSuggestion(@RequestBody Collection<Long> columns) throws DraggerException {
        ArrayList suggestions = new ArrayList();

        generator.findConnectionsBetweenSources(getSources(getColumnFromIds(columns)))
                .stream().forEach(sourceConnection -> suggestions.addAll(sourceConnection.getEdges()));

        return suggestions;
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

    private QueryColumn findColumnById(long columnId) throws DraggerException {
        Optional<QueryColumn> requestedColumn = columnRepository.findById(columnId);

        if (requestedColumn.isPresent()) {
            return requestedColumn.get();
        }

        throw new DraggerException("Column id:" + columnId + " not found");
    }
}