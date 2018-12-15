package dragger.controllers;

import static java.util.Arrays.asList;

import java.sql.JDBCType;
import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ColumnController {

	@GetMapping("api/columns/availableDataTypes")
	public Collection<JDBCType> getAvailableDataTypes() {
		return asList(JDBCType.NUMERIC, JDBCType.VARCHAR, JDBCType.DATE, JDBCType.BOOLEAN);
	}
}
