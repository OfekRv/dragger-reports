package dragger.bl.generator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.entities.SourceConnection;
import dragger.exceptions.DraggerConnectionException;

public interface ConnectionFinder {
	public default Collection<SourceConnection> findConnectionsBetweenSources(Collection<QuerySource> sources)
			throws DraggerConnectionException {
		Collection<SourceConnection> connections = new ArrayList<>();
		Collection<QuerySource> visited = new ArrayList<>();
		Collection<QuerySource> needToBeFoundSources = new ArrayList<>(sources);
		needToBeFoundSources.remove(findFirstElement(sources));
		Queue<Map.Entry<QuerySource, Collection<SourceConnection>>> toVisit = new LinkedList<>();
		toVisit.add(mapSourceToConnections(findFirstElement(sources), null));

		while (!toVisit.isEmpty()) {
			Map.Entry<QuerySource, Collection<SourceConnection>> source = toVisit.poll();
			visited.add(source.getKey());

			for (Map.Entry<QuerySource, SourceConnection> neighour : getSourceNeighbours(source.getKey()).entrySet()) {
				if (!visited.contains(neighour.getKey())) {
					Collection<SourceConnection> deepConnections = findDeepConnectionsBetweenRootAndSourceNeighbour(
							source, neighour);

					if (isDesiredSourceFound(sources, connections, neighour)) {
						connections.addAll(deepConnections);
						needToBeFoundSources.remove(neighour.getKey());
					}

					toVisit.add(mapSourceToConnections(neighour.getKey(), deepConnections));
				}
			}
		}

		// if we want exception when two sources used without connection
		if (needToBeFoundSources.size() > 0) {
			throw new DraggerConnectionException("Could not find connection for source: " + needToBeFoundSources);
		}

		return connections;
	}

	public default boolean isAllSourcesConnected(Collection<QuerySource> sources) {
		if (sources.size() <= 1) {
			return true;
		}

		try {
			findConnectionsBetweenSources(sources);
			return true;
		} catch (DraggerConnectionException e) {
			return false;
		}
	}

	public default Map<QuerySource, SourceConnection> getSourceNeighbours(QuerySource source) {
		Map<QuerySource, SourceConnection> sourcesConnections = new HashMap<>();

		for (SourceConnection connection : getSourceConnections(source)) {
			for (QueryColumn column : connection.getEdges()) {
				if (column.getSource() != source)
					sourcesConnections.put(column.getSource(), connection);
			}
		}

		return sourcesConnections;
	}

	public default Collection<SourceConnection> findDeepConnectionsBetweenRootAndSourceNeighbour(
			Map.Entry<QuerySource, Collection<SourceConnection>> source,
			Map.Entry<QuerySource, SourceConnection> neighour) {
		Collection<SourceConnection> deepConnections = new ArrayList<>();
		if (source.getValue() != null) {
			deepConnections.addAll(source.getValue());
		}
		deepConnections.add(neighour.getValue());
		return deepConnections;
	}

	public static Collection<SourceConnection> getSourceConnections(QuerySource source) {
		Collection<SourceConnection> currentSourceConnections = new ArrayList<>();
		for (QueryColumn column : source.getColumns()) {
			currentSourceConnections.addAll(column.getConnections());
		}

		return currentSourceConnections;
	}

	public static boolean isDesiredSourceFound(Collection<QuerySource> sources,
			Collection<SourceConnection> connections, Map.Entry<QuerySource, SourceConnection> source) {
		return sources.contains(source.getKey()) && !connections.contains(source.getValue());
	}

	public static AbstractMap.SimpleEntry<QuerySource, Collection<SourceConnection>> mapSourceToConnections(
			QuerySource source, Collection<SourceConnection> connections) {
		return new AbstractMap.SimpleEntry<QuerySource, Collection<SourceConnection>>(source, connections);
	}

	public static <T> T findFirstElement(Collection<T> collection) {
		return collection.stream().findFirst().get();
	}
}
