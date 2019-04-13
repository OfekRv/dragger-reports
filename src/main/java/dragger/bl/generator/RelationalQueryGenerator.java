package dragger.bl.generator;

import static java.util.Arrays.asList;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Function;

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
	private static final int SECOND_SOURCE_INDEX = 1;
	private static final int FIRST_SOURCE_INDEX = 0;
	private static final int EDGES_COUNT = 2;
	private static final int FIRST_EDGE = 1;
	private static final int FIRST_EDGE_INDEX = 0;
	private static final int SECOND_EDGE_INDEX = 1;
	private static final String ON = "ON ";
	private static final String INNER_JOIN = "INNER JOIN ";
	private static final String EQUALS = "=";
	private static final String AND = " AND ";
	private static final String SELECT = "SELECT ";
	private static final String FROM = "FROM ";
	private static final String GROUP_BY = "GROUP BY ";
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
		Collection<QuerySource> sources = query.getSources();
		QuerySource baseSource;
		if (isCountQuery(query)) {
			QueryColumn countColumn = query.getCountColumns().stream().findFirst().get();
			rawQuery.add(generateRawClause(SELECT + rawAndNamedCountColumn(countColumn) + SEPERATOR, SEPERATOR,
					query.getColumns(), this::rawAndNamedColumn));
			baseSource = countColumn.getSource();
		} else {
			if (showDuplicates) {
				rawQuery.add(generateRawClause(SELECT, SEPERATOR, query.getColumns(), this::rawAndNamedColumn));
			} else {
				rawQuery.add(generateRawClause(SELECT + SPACE + DISTINCT, SEPERATOR, query.getColumns(),
						this::rawAndNamedColumn));
			}

			baseSource = getBaseSourceFromQuery(query);
		}

		rawQuery.add(generateRawClause(FROM, SEPERATOR, asList(baseSource), this::rawAndNamedSource));

		if (isMultipeSourcesQuery(sources)) {
			Collection<SourceConnection> connections = findConnectionsBetweenSources(sources);
			rawQuery.add(generateJoinClause(connections, baseSource));
		}

		if (containsFilters(filters)) {
			rawQuery.add(generateRawClause(WHERE, AND, filters, this::rawFilter));
		}

		if (isGroupByQuery(query)) {
			rawQuery.add(generateRawClause(GROUP_BY, SEPERATOR, query.getGroupBys(), this::columnWithSource));
		}

		return rawQuery.toString();
	}

	private String generateJoinClause(Collection<SourceConnection> connections, QuerySource baseSource) {
		StringJoiner rawJoin = new StringJoiner(NEW_LINE);

		Collection<QuerySource> allSources = extractSourcesFromConnections(connections);
		Collection<QuerySource> usedSources = new ArrayList<>();
		usedSources.add(baseSource);

		QueryColumn baseEdge;
		QueryColumn joinEdge;
		QueryColumn[] connectionEdges;
		while (!usedSources.containsAll(allSources)) {
			SourceConnection connection = connections.stream()
					.filter(conn -> isConnectionSourcesExistInSources(conn, usedSources)).findAny().get();

			connectionEdges = extractEdgesFromConnection(connection);
			if (isConnectionSourceExistInSources(connection, FIRST_EDGE_INDEX, usedSources)) {
				baseEdge = connectionEdges[FIRST_EDGE_INDEX];
				joinEdge = connectionEdges[SECOND_EDGE_INDEX];
			} else {
				baseEdge = connectionEdges[SECOND_EDGE_INDEX];
				joinEdge = connectionEdges[FIRST_SOURCE_INDEX];
			}
			rawJoin.add(rawInnerJoin(baseEdge, joinEdge));
			usedSources.add(joinEdge.getSource());
			connections.remove(connection);

		}
		return rawJoin.toString();
	}

	private boolean isCountQuery(Query query) {
		return !(query.getCountColumns() == null || query.getCountColumns().isEmpty());
	}

	private boolean containsFilters(Collection<ReportQueryFilter> filters) {
		return filters != null && filters.size() > 0;
	}

	private boolean isGroupByQuery(Query query) {
		return !(query.getGroupBys() == null || query.getGroupBys().isEmpty());
	}

	private boolean isMultipeSourcesQuery(Collection<QuerySource> sources) {
		return sources.size() > 1;
	}

	private String rawAndNamedCountColumn(QueryColumn col) {
		return "COUNT(" + columnWithSource(col) + ")";
	}

	private String rawAndNamedColumn(QueryColumn col) {
		return QUOT_MARKS + col.getSource().getName() + QUOT_MARKS + DOT + col.getRaw() + AS + QUOT_MARKS
				+ col.getName() + QUOT_MARKS;
	}

	private String rawAndNamedSource(QuerySource source) {
		return source.getFromClauseRaw() + AS + QUOT_MARKS + source.getName() + QUOT_MARKS;
	}

	private String rawInnerJoin(QueryColumn baseEdge, QueryColumn joinEdge) {
		return INNER_JOIN + rawAndNamedSource(joinEdge.getSource()) + SPACE + ON + rawAndNamedEdge(baseEdge) + EQUALS
				+ rawAndNamedEdge(joinEdge);
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

	private String rawAndNamedEdge(QueryColumn edge) {
		return QUOT_MARKS + edge.getSource().getName() + QUOT_MARKS + DOT + edge.getRaw();
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

	private QuerySource getBaseSourceFromQuery(Query query) {
		QuerySource baseSource = isCountQuery(query) ? query.getCountColumns().stream().findFirst().get().getSource()
				: query.getSources().stream().findFirst().get();
		return baseSource;
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

	private QuerySource[] extractSourcesFromConnection(SourceConnection connection) {
		QuerySource[] sources = new QuerySource[EDGES_COUNT];
		QueryColumn[] edges = extractEdgesFromConnection(connection);
		sources[FIRST_SOURCE_INDEX] = edges[FIRST_SOURCE_INDEX].getSource();
		sources[SECOND_SOURCE_INDEX] = edges[SECOND_SOURCE_INDEX].getSource();
		return sources;
	}

	private QueryColumn[] extractEdgesFromConnection(SourceConnection connection) {
		QueryColumn[] edges = new QueryColumn[EDGES_COUNT];
		edges[FIRST_SOURCE_INDEX] = connection.getEdges().stream().findFirst().get();
		edges[SECOND_SOURCE_INDEX] = connection.getEdges().stream().skip(FIRST_EDGE).findFirst().get();
		return edges;
	}

	private boolean isConnectionSourcesExistInSources(SourceConnection connection, Collection<QuerySource> sources) {
		return isConnectionSourceExistInSources(connection, FIRST_EDGE_INDEX, sources)
				|| isConnectionSourceExistInSources(connection, SECOND_EDGE_INDEX, sources);
	}

	private boolean isConnectionSourceExistInSources(SourceConnection connection, int sourceIndex,
			Collection<QuerySource> sources) {
		return sources.contains(extractSourcesFromConnection(connection)[sourceIndex]);
	}
}
