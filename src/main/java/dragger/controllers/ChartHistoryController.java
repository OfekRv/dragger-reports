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
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ChartHistoryController {
    @Autowired
    private ChartExecutionResultRepository executionResultRepository;

    @GetMapping("api/chartExecutionResults/{chartId}/{date}")
    public Collection<ChartExecutionResult> generateFilteredReport(@PathVariable long chartId, @PathVariable String date) throws DraggerException {
        List<ChartExecutionResult> collect = executionResultRepository.findAll().stream().filter(result -> result.getId().getChart().getId() == chartId)
                .filter(result-> isInTheSameWeek(result.getId().getExecutionDate(), LocalDate.parse(date,DateTimeFormatter.ISO_LOCAL_DATE))).collect(Collectors.toList());
        collect.stream().forEach(result -> result.getId().setChart(null));
        return collect;
    }

    private boolean isInTheSameWeek(LocalDate executionDate, LocalDate date) {
        LocalDate today = LocalDate.now();
        int weeksDistanceFromToday = Period.between(date, today).getDays() / 7;
        LocalDate firstDayOfRange = today.minusWeeks(weeksDistanceFromToday);
        LocalDate lastDayOfRange = today.minusWeeks(weeksDistanceFromToday + 1);
        return executionDate.isBefore(firstDayOfRange.plusDays(1)) && executionDate.isAfter(lastDayOfRange);
    }

}