package dragger.bl;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.core.JdbcTemplate;

@Named
public class RationalQueryExecuter implements QueryExecutor {
	@Inject
	JdbcTemplate executer;

	@Override
	public Collection<Map<String, Object>> executeQuery(String query) {
		return executer.queryForList(query);
	}
}
