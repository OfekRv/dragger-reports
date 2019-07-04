package dragger.repositories;

import dragger.entities.charts.Chart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
}
