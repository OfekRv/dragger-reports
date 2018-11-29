package dragger.bl.generator;

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
		LinkedList<QuerySource> toVisit = new LinkedList<>();
		toVisit.add(sources.stream().findFirst().get());

		while (!toVisit.isEmpty()) {
			QuerySource source = toVisit.removeFirst();
			visited.add(source);

			for (Map.Entry<QuerySource, SourceConnection> neighour : getAllSourcesConnectedToSource(source)
					.entrySet()) {
				if (!visited.contains(neighour.getKey())) {
					if (sources.contains(neighour.getKey()) && !connections.contains(neighour.getValue())) {
						connections.add(neighour.getValue());
						needToBeFoundSources.remove(neighour.getKey());
					}
					toVisit.addLast(neighour.getKey());
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
