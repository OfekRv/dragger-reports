package dragger.bl.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dragger.entities.QueryColumn;
import dragger.entities.QuerySource;
import dragger.entities.SourceConnection;
import dragger.exceptions.DraggerConnectionException;

public interface ConnectionFinder {
	public default Collection<SourceConnection> findConnectionsBetweenSources(Collection<QuerySource> sources)
			throws DraggerConnectionException {
		Collection<SourceConnection> connections = new ArrayList<>();
		Collection<QuerySource> needToBeFoundSources = new ArrayList<>(sources);
		for (QuerySource source : sources) {
			for (Map.Entry<QuerySource, SourceConnection> connectoinEntry : getAllSourcesConnectedToSource(source)
					.entrySet()) {
				if (sources.contains(connectoinEntry.getKey())) {
					connections.add(connectoinEntry.getValue());
					needToBeFoundSources.remove(source);
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
