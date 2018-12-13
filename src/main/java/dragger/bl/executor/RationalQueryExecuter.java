package dragger.bl.executor;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import dragger.exceptions.DraggerExportException;

@Named
public class RationalQueryExecuter implements QueryExecutor {
	@Inject
	private JdbcTemplate executer;

	@Override
	public SqlRowSet executeQuery(String query) throws DraggerExportException {
		try (Connection jdbcConn = executer.getDataSource().getConnection()) {
			jdbcConn.setReadOnly(true);
		} catch (SQLException e) {
			throw new DraggerExportException("Could not set connection to read only", e);
		}
		return executer.queryForRowSet(query);
	}
}
