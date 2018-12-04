package dragger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.Filter;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
}
