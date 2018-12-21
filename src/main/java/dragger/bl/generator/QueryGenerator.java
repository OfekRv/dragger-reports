package dragger.bl.generator;

import java.util.Collection;

import dragger.entities.Query;
import dragger.entities.ReportQueryFilter;
import dragger.exceptions.DraggerException;

public interface QueryGenerator extends ConnectionFinder {
	public String generate(Query query, Collection<ReportQueryFilter> filters, boolean showDuplicates)
			throws DraggerException;
}
