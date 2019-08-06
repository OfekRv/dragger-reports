package dragger.controllers;

import java.util.*;
import java.util.stream.Collectors;

import dragger.bl.generator.QueryGenerator;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.entities.SourceConnection;
import dragger.entities.charts.Chart;
import dragger.entities.charts.ChartColumnResult;
import dragger.repositories.QueryColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import dragger.bl.exporter.ChartQueryExporter;
import dragger.exceptions.DraggerControllerReportNotFoundException;
import dragger.exceptions.DraggerException;
import dragger.repositories.ChartRepository;

import javax.transaction.Transactional;

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
    public Collection<ChartColumnResult> generateFilteredReport(@RequestParam long chartId) throws DraggerException {
        Optional<Chart> requestedChart = chartRepository.findById(chartId);

        if (!requestedChart.isPresent()) {
            throw new DraggerControllerReportNotFoundException("Chart id:" + chartId + " not found");
        }

        return exporter.export(requestedChart.get(), requestedChart.get().getFilters());
    }

    @GetMapping("api/charts/filterColumnsSuggestion")
    public Collection<Long> findFilterColumnsSuggestion(@RequestParam Collection<Long> columns) throws DraggerException {
        ArrayList<Long> suggestions = new ArrayList();

        List<QuerySource> sourcesOfColumns = getColumnFromIds(columns).stream().map(QueryColumn::getSource).collect(Collectors.toList());

        if(sourcesOfColumns.get(0).getSourceId() == sourcesOfColumns.get(1).getSourceId())
        {
            suggestions.addAll(sourcesOfColumns.get(0).getColumns().stream().filter(queryColumnToAdd -> queryColumnToAdd.isVisible()).map(QueryColumn::getColumnId).collect(Collectors.toList()));
        }
        else {

            for (SourceConnection connection : generator.findConnectionsBetweenSources(sourcesOfColumns)) {
                connection.getEdges().stream().filter(queryColumn -> queryColumn.getSource().isVisible())
                        .forEach(queryColumn ->
                                suggestions.addAll(queryColumn.getSource().getColumns().stream()
                                        .filter(queryColumnToAdd -> queryColumnToAdd.isVisible()).map(QueryColumn::getColumnId).collect(Collectors.toList())));
            }
        }

        return suggestions.stream().distinct().collect(Collectors.toList());
    }

    @Transactional
    @PutMapping("api/charts/updateChartName")
    public void updateChartName(@RequestParam long chartId, @RequestBody String newName) throws DraggerException {
        findChartById(chartId).setName(newName);
    }

    private Chart findChartById(long chartId) throws DraggerException {
        Optional<Chart> requestedChart = chartRepository.findById(chartId);

        if (requestedChart.isPresent()) {
            return requestedChart.get();
        }

        throw new DraggerException("Chart id:" + chartId + " not found");
    }

    private Collection<QueryColumn> getColumnFromIds(Collection<Long> columnsResources) throws DraggerException {
        Collection<QueryColumn> columns = new ArrayList<>();

        for (Long columnId : columnsResources) {
            Optional<QueryColumn> requestedColumn = columnRepository.findById(columnId);

            if (!requestedColumn.isPresent()) {
                throw new DraggerException("Column id:" + columnId + " not found");
            }

            columns.add(requestedColumn.get());
        }
        return columns;
    }
}