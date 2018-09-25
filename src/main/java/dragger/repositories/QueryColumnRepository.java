package dragger.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dragger.entities.QueryColumn;

@Repository
public interface QueryColumnRepository extends JpaRepository<QueryColumn, Long> {
	public Collection<QueryColumn> findQueryColumnsBySourceSourceId(long sourceId);

	public Collection<QueryColumn> findConnectionsByColumnId(long queryColumnId);
}
