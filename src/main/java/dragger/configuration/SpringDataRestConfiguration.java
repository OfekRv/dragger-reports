package dragger.configuration;

import dragger.entities.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class SpringDataRestConfiguration extends RepositoryRestConfigurerAdapter {
	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(Report.class);
		config.exposeIdsFor(Chart.class);
		config.exposeIdsFor(Filter.class);
		config.exposeIdsFor(QueryColumn.class);
		config.exposeIdsFor(Dashboard.class);
	}
}
