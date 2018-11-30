package dragger.bl.generator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
		needToBeFoundSources.remove(sources.stream().findFirst().get());
		LinkedList<Map.Entry<QuerySource, Collection<SourceConnection>>> toVisit = new LinkedList<>();
		toVisit.add(new AbstractMap.SimpleEntry<QuerySource, Collection<SourceConnection>>(
				sources.stream().findFirst().get(), null));

		while (!toVisit.isEmpty()) {
			Map.Entry<QuerySource, Collection<SourceConnection>> source = toVisit.removeFirst();
			visited.add(source.getKey());

			for (Map.Entry<QuerySource, SourceConnection> neighour : getAllSourcesConnectedToSource(source.getKey())
					.entrySet()) {
				Collection<SourceConnection> deepConnections = findDeepConnectionsBetweenRootAndSourceNeighbour(source, neighour);
				if (!visited.contains(neighour.getKey())) {
					if (sources.contains(neighour.getKey()) && !connections.contains(neighour.getValue())) {
						connections.addAll(deepConnections);
						needToBeFoundSources.remove(neighour.getKey());
					}

					toVisit.addLast(new AbstractMap.SimpleEntry<QuerySource, Collection<SourceConnection>>(
							neighour.getKey(), deepConnections));
				}
			}
		}

		// if we want exception when two sources used without connection
		if (needToBeFoundSources.size() > 0) {
			throw new DraggerConnectionException("Could not find connection for source: " + needToBeFoundSources);
		}

		return connections;
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

	public default Map<QuerySource, SourceConnection> getAllSourcesConnectedToSource(QuerySource source) {
		Map<QuerySource, SourceConnection> sourcesConnections = new HashMap<>();

		for (SourceConnection connection : getSourceConnections(source)) {
			for (QueryColumn column : connection.getEdges()) {
				if (column.getSource() != source)
					sourcesConnections.put(column.getSource(), connection);
			}
		}

		return sourcesConnections;
	}

	public default Collection<SourceConnection> getSourceConnections(QuerySource source) {
		Collection<SourceConnection> currentSourceConnections = new ArrayList<>();
		for (QueryColumn column : source.getColumns()) {
			currentSourceConnections.addAll(column.getConnections());
		}

		return currentSourceConnections;
	}
}
