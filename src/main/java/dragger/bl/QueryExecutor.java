package dragger.bl;

import java.util.Collection;
import java.util.Map;

public interface QueryExecutor {
	public Collection<Map<String, Object>> executeQuery(String query);
}
