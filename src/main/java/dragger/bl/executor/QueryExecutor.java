package dragger.bl.executor;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import dragger.exceptions.DraggerExecuteException;

public interface QueryExecutor {
	public SqlRowSet executeQuery(String query) throws DraggerExecuteException;
}