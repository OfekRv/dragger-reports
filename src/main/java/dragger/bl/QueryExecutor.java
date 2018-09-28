package dragger.bl;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface QueryExecutor {
	public SqlRowSet executeQuery(String query);
}
