package dragger.bl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.core.JdbcTemplate;

import dragger.entities.Query;
import dragger.entities.Report;

@Named
public class ExcelReportExporter implements ReportExporter {
	@Inject
	QueryGenerator generator;
	@Inject
	JdbcTemplate executer;

	@Override
	public File export(Report reportToExport) throws IOException {
		// TODO: file stub
		new File("file1.xls").createNewFile();
		return new File("file1.xls");
	}

	private Collection<Map<String, Object>> executeQuery(Query query) {
		return executer.queryForList(generator.generate(query));
	}
}
