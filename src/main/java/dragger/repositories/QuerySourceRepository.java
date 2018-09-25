package dragger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.QuerySource;

@Repository
public interface QuerySourceRepository extends JpaRepository<QuerySource, Long> {

}
