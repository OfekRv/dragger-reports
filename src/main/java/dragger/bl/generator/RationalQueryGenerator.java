package dragger.bl.generator;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Function;

import javax.inject.Named;

import dragger.entities.Query;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.entities.SourceConnection;
import dragger.exceptions.DraggerConnectionException;
import dragger.exceptions.DraggerException;

@Named
public class RationalQueryGenerator implements QueryGenerator {
	private static final String EQUALS = "=";
	private static final String AND = " AND ";
	private static final String SELECT = "SELECT ";
	private static final String FROM = "FROM ";
	private static final String WHERE = "WHERE ";
	private static final String AS = " AS ";
	private static final String DOT = ".";
	private static final String QUOT_MARKS = "\"";
	private static final String NEW_LINE = " \n";
	private static final String SEPERATOR = ", ";

	public String generate(Query query) throws DraggerException {
		StringJoiner rawQuery = new StringJoiner(NEW_LINE);

		rawQuery.add(generateRawClause(SELECT, SEPERATOR, query.getColumns(), this::rawAndNamedColumn));
		rawQuery.add(generateRawClause(FROM, SEPERATOR, query.getSources(), this::rawAndNamedSource));

		if (query.getSources().size() > 1) {
			try {
				rawQuery.add(generateRawClause(WHERE, AND, findConnectionsBetweenSources(query.getSources()),
						this::rawConnection));
			} catch (DraggerConnectionException e) {
				throw new DraggerException("Could not generate Query", e);
			}
		}

		return rawQuery.toString();
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

	private StringJoiner rawAndNamedEdge(QueryColumn edge, StringJoiner raw) {
		return raw.add(QUOT_MARKS + edge.getSource().getName() + QUOT_MARKS + DOT + edge.getRaw());
	}

	private <T> String generateRawClause(String clauseTypeRaw, String delimiter, Collection<T> clauseItems,
			Function<T, String> generateFunc) {
		StringJoiner raw = new StringJoiner(delimiter);
		clauseItems.stream().forEach(item -> raw.add(generateFunc.apply(item)));
		return clauseTypeRaw + raw.toString();
	}
}
