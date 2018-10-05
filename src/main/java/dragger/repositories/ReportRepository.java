package dragger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
