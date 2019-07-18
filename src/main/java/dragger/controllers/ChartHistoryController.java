package dragger.controllers;

import dragger.entities.charts.Chart;
import dragger.entities.charts.ChartColumnResult;
import dragger.entities.charts.ChartExecutionResult;
import dragger.exceptions.DraggerControllerReportNotFoundException;
import dragger.exceptions.DraggerException;
import dragger.repositories.ChartExecutionResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ChartHistoryController {
    @Autowired
    private ChartExecutionResultRepository executionResultRepository;

    @GetMapping("api/chartExecutionResults/{chartId}")
    public Collection<ChartExecutionResult> generateFilteredReport(@PathVariable long chartId) throws DraggerException {
        return executionResultRepository.findAll().stream().filter(result-> result.getId().getChart().getId() == chartId).collect(Collectors.toList());
    }

}