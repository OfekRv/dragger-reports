package dragger.bl.executor;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import dragger.exceptions.DraggerExportException;

@Named
public class RationalQueryExecuter implements QueryExecutor {
	@Inject
	JdbcTemplate executer;

	public RationalQueryExecuter() throws DraggerExportException {
		try {
			executer.getDataSource().getConnection().setReadOnly(true);
		} catch (SQLException e) {
			throw new DraggerExportException("Could not set connection to read only", e);
		}
	}

	@Override
	public SqlRowSet executeQuery(String query) {
		return executer.queryForRowSet(query);
	}
}
