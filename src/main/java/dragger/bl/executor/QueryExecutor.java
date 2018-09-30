package dragger.bl.executor;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface QueryExecutor {
	public SqlRowSet executeQuery(String query);
}
