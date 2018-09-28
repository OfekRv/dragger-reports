package dragger.bl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@Named
public class RationalQueryExecuter implements QueryExecutor {
	@Inject
	JdbcTemplate executer;

	@Override
	public SqlRowSet executeQuery(String query) {
		return executer.queryForRowSet(query);
	}
}
