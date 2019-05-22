package dragger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.Dashboard;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
}
