package dragger.bl;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Function;

import javax.inject.Named;

import dragger.entities.Query;
import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.entities.SourceConnection;

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

	public String generate(Query query) {
		StringJoiner rawQuery = new StringJoiner(NEW_LINE);

		rawQuery.add(generateRawClause(SELECT, SEPERATOR, query.getColumns(), this::rawAndNamedColumn));
		rawQuery.add(generateRawClause(FROM, SEPERATOR, query.getSources(), this::rawAndNamedSource));
		rawQuery.add(generateRawClause(WHERE, AND, query.getConnections(), this::rawConnection));

		return rawQuery.toString();
	}

	private <T> String generateRawClause(String clauseTypeRaw, String delimiter, Collection<T> clauseItems,
			Function<T, String> generateFunc) {
		StringJoiner raw = new StringJoiner(delimiter);
		clauseItems.stream().forEach(item -> raw.add(generateFunc.apply(item)));
		return clauseTypeRaw + raw.toString();
	}

	private String rawAndNamedColumn(QueryColumn col) {
		return col.getRaw() + AS + QUOT_MARKS + col.getName() + QUOT_MARKS;
	}

	private String rawAndNamedSource(QuerySource source) {
		return source.getFromClauseRaw() + AS + QUOT_MARKS + source.getName() + QUOT_MARKS;
	}

	private String rawConnection(SourceConnection connection) {
		return QUOT_MARKS + connection.getFirstEdge().getSource().getName() + QUOT_MARKS + DOT
				+ connection.getFirstEdge().getRaw() + EQUALS + QUOT_MARKS
				+ connection.getSecondEdge().getSource().getName() + QUOT_MARKS + DOT
				+ connection.getSecondEdge().getRaw();

		/*
		 * return QUOT_MARKS + connection.getFirstEdgeSource().getName() +
		 * QUOT_MARKS + DOT + connection.getFirstEdge().getRaw() + EQUALS +
		 * QUOT_MARKS + connection.getSecondEdgeSource().getName() + QUOT_MARKS
		 * + DOT + connection.getSecondEdge().getRaw();
		 */
	}
}
