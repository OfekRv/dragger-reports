package dragger.bl.generator;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.springframework.jdbc.support.JdbcUtils;

import dragger.entities.Query;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.entities.ReportQueryFilter;
import dragger.entities.SourceConnection;
import dragger.exceptions.DraggerException;

@Named
public class RelationalQueryGenerator implements QueryGenerator {
	private static final String EQUALS = "=";
	private static final String AND = " AND ";
	private static final String SELECT = "SELECT ";
	private static final String FROM = "FROM ";
	private static final String WHERE = "WHERE ";
	private static final String AS = " AS ";
	private static final String DOT = ".";
	private static final String QUOT_MARKS = "\"";
	private static final String QUOT_MARK = "'";
	private static final String SPACE = " ";
	private static final String NEW_LINE = " \n";
	private static final String SEPERATOR = ", ";
	private static final String DISTINCT = "DISTINCT ";
	private static final String EMPTY_STRING = "";

	public String generate(Query query, Collection<ReportQueryFilter> filters, boolean showDuplicates)
			throws DraggerException {
		StringJoiner rawQuery = new StringJoiner(NEW_LINE);
		if (showDuplicates) {
			rawQuery.add(generateRawClause(SELECT, SEPERATOR, query.getColumns(), this::rawAndNamedColumn));
		} else {
			rawQuery.add(generateRawClause(SELECT + SPACE + DISTINCT, SEPERATOR, query.getColumns(),
					this::rawAndNamedColumn));
		}

		Collection<QuerySource> sources = query.getSources();

		if (isMultipeSourcesQuery(sources)) {
			Collection<SourceConnection> connections = findConnectionsBetweenSources(sources);
			sources.addAll(extractSourcesFromConnections(connections));
			sources = sources.stream().distinct().collect(Collectors.toList());
			rawQuery.add(generateRawClause(FROM, SEPERATOR, sources, this::rawAndNamedSource));
			rawQuery.add(generateRawClause(WHERE, AND, connections, this::rawConnection));

		} else {
			rawQuery.add(generateRawClause(FROM, SEPERATOR, sources, this::rawAndNamedSource));
		}

		if (filters != null && filters.size() > 0) {
			if (isMultipeSourcesQuery(sources)) {
				// we are already in WHERE so we use it as AND
				rawQuery.add(generateRawClause(AND, AND, filters, this::rawFilter));
			} else {
				rawQuery.add(generateRawClause(WHERE, AND, filters, this::rawFilter));
			}
		}

		return rawQuery.toString();

	}

	private boolean isMultipeSourcesQuery(Collection<QuerySource> sources) {
		return sources.size() > 1;
	}

	private String rawAndNamedColumn(QueryColumn col) {
		return QUOT_MARKS + col.getSource().getName() + QUOT_MARKS + DOT + col.getRaw() + AS + QUOT_MARKS
				+ col.getName() + QUOT_MARKS;
	}

	private String rawAndNamedSource(QuerySource source) {
		return source.getFromClauseRaw() + AS + QUOT_MARKS + source.getName() + QUOT_MARKS;
	}

	private String rawConnection(SourceConnection connection) {
		StringJoiner raw = new StringJoiner(EQUALS);
		connection.getEdges().stream().forEach(edge -> rawAndNamedEdge(edge, raw));
		return raw.toString();
	}

	private String rawFilter(ReportQueryFilter filter) {
		StringJoiner raw = new StringJoiner(SPACE);
		raw.add(rawFilterValue(filter));
		raw.add(filter.getFilter().getRawFilter());
		raw.add(columnWithSource(filter.getColumn()));
		return raw.toString();
	}

	private String rawFilterValue(ReportQueryFilter filter) {
		JDBCType columnType = filter.getColumn().getDataType();
		if (JdbcUtils.isNumeric(columnType.ordinal())) {
			return filter.getValue();
		} else {
			return QUOT_MARK + filter.getValue() + QUOT_MARK;
		}
	}

	private String columnWithSource(QueryColumn column) {
		StringJoiner raw = new StringJoiner(DOT);
		raw.add(QUOT_MARKS + column.getSource().getName() + QUOT_MARKS);
		raw.add(column.getRaw());
		return raw.toString();
	}

	private StringJoiner rawAndNamedEdge(QueryColumn edge, StringJoiner raw) {
		return raw.add(QUOT_MARKS + edge.getSource().getName() + QUOT_MARKS + DOT + edge.getRaw());
	}

	private <T> String generateRawClause(String clauseTypeRaw, String delimiter, Collection<T> clauseItems,
			Function<T, String> generateFunc) {
		if (!clauseItems.isEmpty()) {
			StringJoiner raw = new StringJoiner(delimiter);
			clauseItems.stream().forEach(item -> raw.add(generateFunc.apply(item)));
			return clauseTypeRaw + raw.toString();
		}
		return EMPTY_STRING;
	}

	private Collection<QuerySource> extractSourcesFromConnections(Collection<SourceConnection> connections) {
		Collection<QuerySource> sources = new ArrayList<>();
		for (SourceConnection connection : connections) {
			for (QueryColumn column : connection.getEdges()) {
				sources.add(column.getSource());
			}
		}
		return sources;
	}
}
