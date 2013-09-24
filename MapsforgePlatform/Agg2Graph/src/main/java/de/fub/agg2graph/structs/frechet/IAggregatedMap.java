package de.fub.agg2graph.structs.frechet;

import java.util.Collection;
import java.util.Iterator;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggNode;

public interface IAggregatedMap extends SearchIndex<AggNode>, Iterable<AggNode> {
	
//	public void insertEdge(GPSEdge edge);
	public void insertConnection(AggConnection conn);
	
//	public Collection<AggConnection> getInEdges(GPSPoint location, Collection<AggConnection> inEdges);
	public Collection<AggConnection> getInConnections(AggNode location, Collection<AggConnection> inConnection);

//	public Collection<AggConnection> getOutEdges(GPSPoint location, Collection<AggConnection> outEdges);
	public Collection<AggConnection> getOutConnections(AggNode location, Collection<AggConnection> outConnection);
	
//	public Collection<AggConnection> edges();
	public Collection<AggConnection> connection();
	
//	public boolean removeEdge(GPSEdge edge);
	public boolean removeConnections(AggConnection conn);

//	public void updateLocation(GPSPoint location, double dLongitude, double dLatitude);
	public void updateLocation(AggNode location, double dLongitude, double dLatitude);
	
	/**
	 * @post the node has the new values from neu.
	 */
	public void updateLocation(AggNode old, AggNode neu);
	
	// Depth first search paths in the graph by weighting the edges with the valueFunc.
	public Iterator<AggConnection> connectionsIterator(AggNode start, WeightFunc weightFunc);
	
	public void decorate(AggNode location, Object decoration);
	
	public void decorate(AggConnection edge, Object decoration);

	public boolean isEmpty();
}
