package dragger.controllers;

import java.util.*;
import java.util.stream.Collectors;

import dragger.bl.generator.QueryGenerator;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.entities.SourceConnection;
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
                                suggestions.addAll(queryColumn.getSource().getColumns().stream().filter(queryColumnToAdd -> queryColumnToAdd.isVisible())
                                        .map(QueryColumn::getColumnId).distinct().collect(Collectors.toList())));
            }
        }

        return suggestions;
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