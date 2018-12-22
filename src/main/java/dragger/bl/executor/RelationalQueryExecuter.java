package dragger.bl.executor;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import dragger.exceptions.DraggerExecuteException;

@Named
public class RelationalQueryExecuter implements QueryExecutor {
	@Inject
	private JdbcTemplate executer;

	@Override
	public SqlRowSet executeQuery(String query) throws DraggerExecuteException {
		try (Connection jdbcCon = executer.getDataSource().getConnection()) {
			jdbcCon.setReadOnly(true);
			return executer.queryForRowSet(query);
		} catch (SQLException e) {
			throw new DraggerExecuteException("Could not set connection to read only", e);
		}
	}
}