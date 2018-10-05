package dragger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.SourceConnection;

@Repository
public interface SourceConnectionReposiroty extends JpaRepository<SourceConnection, Long> {
}
