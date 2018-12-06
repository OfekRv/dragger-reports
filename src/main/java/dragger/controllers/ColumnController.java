package dragger.controllers;

import static java.util.Arrays.asList;

import java.sql.JDBCType;
import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dragger.entities.DataType;

@RestController
public class ColumnController {

	@GetMapping("api/columns/availableDataTypes")
	public Collection<DataType> getAvailableDataTypes() {
		return asList(new DataType(JDBCType.NUMERIC), new DataType(JDBCType.VARCHAR), new DataType(JDBCType.DATE));
	}
}
