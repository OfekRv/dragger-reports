package dragger.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.charts.ChartExecutionResult;
import dragger.entities.charts.ChartExecutionResultId;

@Repository


public interface ChartExecutionResultRepository extends JpaRepository<ChartExecutionResult, ChartExecutionResultId> {

}