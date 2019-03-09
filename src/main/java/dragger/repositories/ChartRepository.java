package dragger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.Chart;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
}
