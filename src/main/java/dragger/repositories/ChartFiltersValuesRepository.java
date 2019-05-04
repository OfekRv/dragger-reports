package dragger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.ChartQueryFilter;

@Repository
public interface ChartFiltersValuesRepository extends JpaRepository<ChartQueryFilter, Long> {
}
