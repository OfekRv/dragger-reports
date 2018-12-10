package dragger.controllers;

import static java.util.Arrays.asList;
import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dragger.enums.DataType;

@RestController
public class ColumnController {

	@GetMapping("api/columns/availableDataTypes")
	public Collection<DataType> getAvailableDataTypes() {
		return asList(DataType.NUMBER,DataType.TEXT,DataType.DATE);
	}
}
