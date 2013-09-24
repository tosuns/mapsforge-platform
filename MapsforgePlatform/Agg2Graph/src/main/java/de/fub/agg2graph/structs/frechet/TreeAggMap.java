package de.fub.agg2graph.structs.frechet;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSRegion;
import de.fub.agg2graph.structs.ILocation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class TreeAggMap extends TreeMap<AggNode, AggNode> implements
        IAggregatedMap {

    private static final long serialVersionUID = -1827387769631060525L;

    class GPSEdgeIterator implements Iterator<AggConnection> {

        final WeightFunc weightFunc;
        final HashSet<AggNode> notVisited;
        AggNode position = null;
        public AggContainer aggContainer;

        public GPSEdgeIterator(GPSPoint start, WeightFunc weightFunc,
                AggContainer aggContainer) {
            if (!containsKey(start)) {
                throw new NoSuchElementException();
            }

            position = get(start);
            this.weightFunc = weightFunc;
            this.notVisited = new HashSet<>(getAllEdgeStarts());
            this.aggContainer = aggContainer;
        }

        @Override
        public boolean hasNext() {
            return !(position == null || weightFunc.isFinish() || notVisited
                    .isEmpty());
        }

        @Override
        public AggConnection next() {
            if (position.isSink()) {
                if (notVisited.isEmpty()) {
                    assert (false); // Here shouldn't we are!
                } else {
					// Look for more nodes. Optimally we start with a source
                    // node.
                    position = searchNextSource();
                }
            }

			// GPSEdge edgeToReturn = Collections.min(position.out, new
            // Comparator<GPSEdge>() {
            HashSet<GPSEdge> posOut = new HashSet<GPSEdge>();
            for (AggConnection agg : position.out) {
                posOut.add(new GPSEdge(agg));
            }
            GPSEdge edgeToReturn = Collections.min(posOut,
                    new Comparator<GPSEdge>() {
                        @Override
                        public int compare(GPSEdge o1, GPSEdge o2) {
                            return Double.compare(weightFunc.weight(o1),
                                    weightFunc.weight(o2));
                        }
                    });

			// Determine next position.
            // First follow the path. If we hopped to a sink - search for good
            // new start.
            position = get(edgeToReturn.getTo());

            notVisited.remove(edgeToReturn.getFrom());

            return new AggConnection(new AggNode(edgeToReturn.getFrom(),
                    this.aggContainer), new AggNode(edgeToReturn.getTo(),
                            this.aggContainer), this.aggContainer);
            // edgeToReturn, this.aggContainer);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported.");
        }

        private AggNode searchNextSource() {
            for (ILocation key : notVisited) {
                if (get(key).isSource()) {
                    return get(key);
                }
            }

            // No source found return first element found or null.
            return get(notVisited.iterator().next());
        }
    }

	// ---- 2D Search index
    // ------------------------------------------------------------
    KdTreeIndex<AggNode> searchIndex;
    private boolean searchIndexNeedsRebuild = true;
    public AggContainer aggContainer;

    public TreeAggMap(AggContainer aggContainer) {
        this.aggContainer = aggContainer;
    }

    public boolean isSource(ILocation location) {
        if (containsKey(location)) {
            return ((AggNode) get(location)).isSource();
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean isSink(ILocation location) {
        if (containsKey(location)) {
            return ((AggNode) get(location)).isSink();
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean isRegular(ILocation location) {
        if (containsKey(location)) {
            return ((AggNode) get(location)).isRegular();
        } else {
            throw new NoSuchElementException();
        }
    }

    private Collection<AggNode> getAllEdgeStarts() {
        ArrayList<AggNode> result = new ArrayList<>();
        for (AggNode node : values()) {
			// Enough to return all start points of outgoing edges to get a
            // complete list of all edges in the graph.
            for (AggConnection edge : node.out) {
                result.add(edge.getFrom());
            }
        }
        return result;
    }

    @Override
    public AggNode searchNN(AggNode point) {
        if (searchIndexNeedsRebuild) {
            searchIndex = new KdTreeIndex<AggNode>(keySet());
            searchIndexNeedsRebuild = false;
        }
        return searchIndex.searchNN(point);
    }

    @Override
    public List<AggNode> searchKnn(AggNode point, int k) {
        if (searchIndexNeedsRebuild) {
            searchIndex = new KdTreeIndex<AggNode>(keySet());
            searchIndexNeedsRebuild = false;
        }
        return searchIndex.searchKnn(point, k);
    }

    @Override
    public List<AggNode> searchRegion(GPSRegion region) {
        if (searchIndexNeedsRebuild) {
            searchIndex = new KdTreeIndex<AggNode>(keySet());
            searchIndexNeedsRebuild = false;
        }
        return searchIndex.searchRegion(region);
    }

    @Override
    public Iterator<AggNode> iterator() {
        return keySet().iterator();
    }

    @Override
    public void insertConnection(AggConnection conn) {
        AggNode from = new AggNode(conn.getFrom(), this.aggContainer);
        AggNode to = new AggNode(conn.getTo(), this.aggContainer);
        AggConnection aggEdge = new AggConnection(from, to, this.aggContainer);

        AggNode nodeFrom = null;
        if (containsKey(from)) {
            nodeFrom = get(from);
            aggEdge.setFrom(nodeFrom);
        } else {
            nodeFrom = new AggNode(from, this.aggContainer);
            put(from, nodeFrom);
        }
        nodeFrom.out.add(new AggConnection(aggEdge));

        AggNode nodeTo = null;
        if (containsKey(to)) {
            nodeTo = get(to);
            aggEdge.setTo(nodeTo);
        } else {
            nodeTo = new AggNode(to, this.aggContainer);
            put(to, nodeTo);
        }
        nodeTo.in.add(new AggConnection(aggEdge));
    }

    @Override
    public Collection<AggConnection> getInConnections(AggNode location,
            Collection<AggConnection> inConnection) {
        Collection<AggConnection> result = Collections.emptyList();
        if (inConnection != null) {
            result = inConnection;
            result.addAll(get(location).in);
        } else if (containsKey(location) && get(location) != null) {
            result = get(location).in;
        }
        return result;
    }

    @Override
    public Collection<AggConnection> getOutConnections(AggNode location,
            Collection<AggConnection> outConnection) {
        Collection<AggConnection> result = Collections.emptyList();
        if (outConnection != null) {
            result = outConnection;
            result.addAll(get(location).out);
        } else if (containsKey(location) && get(location) != null) {
            result = get(location).out;
        }
        return result;
    }

    @Override
    public Collection<AggConnection> connection() {
        ArrayList<AggConnection> result = new ArrayList<AggConnection>();
        for (AggNode node : values()) {
			// Enough to return all start points of outgoing edges to get a
            // complete
            // list of all edges in the graph.
            result.addAll(node.out);
        }
        return result;
    }

    @Override
    public boolean removeConnections(AggConnection conn) {
        assert (containsKey(conn.getFrom()) && containsKey(conn.getTo()));

        boolean isRemoveComplete = false;
        AggNode nodeFrom = get(conn.getFrom());

        Iterator<AggConnection> it = nodeFrom.out.iterator();
        while (it.hasNext()) {
            AggConnection outConn = new AggConnection(it.next());
            if (outConn.compareTo(conn) == 0) {
                it.remove();
            }
        }

        if (nodeFrom.isEmpty()) {
            isRemoveComplete &= (remove(nodeFrom) != null);
        }

        AggNode nodeTo = get(conn.getTo());
        while (it.hasNext()) {
            AggConnection inConn = new AggConnection(it.next());
            // it.next();
            if (inConn.compareTo(conn) == 0) {
                it.remove();
            }
        }
        if (nodeTo.isEmpty()) {
            isRemoveComplete &= (remove(nodeTo) != null);
        }

        searchIndexNeedsRebuild = true;

        return isRemoveComplete;
    }

    @Override
    public void updateLocation(AggNode location, double dLongitude,
            double dLatitude) {
        assert (containsKey(location));
        if (!containsKey(location)) {
            return;
        }

        AggNode node = remove(location);
        if (node == null) {
            throw new NoSuchElementException();
        }

		// The edges should be connected well, such that their locations moved
        // also.
        node.setLon(location.getLon() + dLongitude);
        node.setLat(location.getLat() + dLatitude);

        // Add again with new position.
        put(node, node);

        searchIndexNeedsRebuild = true;
    }

    @Override
    public void updateLocation(AggNode old, AggNode neu) {
        double dLongitude = neu.getLon() - old.getLon();
        double dLatitude = neu.getLat() - old.getLat();
        updateLocation(old, dLongitude, dLatitude);
    }

    @Override
    public Iterator<AggConnection> connectionsIterator(AggNode start,
            WeightFunc weightFunc) {
        return new GPSEdgeIterator(start, weightFunc, this.aggContainer);
    }

    @Override
    public void decorate(AggNode location, Object decoration) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void decorate(AggConnection edge, Object decoration) {
        throw new UnsupportedOperationException("not implemented");
    }
}
