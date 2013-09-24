package de.fub.agg2graph.input;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSRegion;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.structs.frechet.ITrace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

public class Trace implements ITrace {

    AggContainer aggContainer;
    protected ArrayList<ILocation> locations = new ArrayList<ILocation>();
    protected ArrayList<AggConnection> conns = new ArrayList<AggConnection>();
    protected ArrayList<GPSEdge> edges = new ArrayList<GPSEdge>();
    protected String name;

    long id;
    static long idGenerator = 0;
    public boolean connsNeedsUpdate = true;
    public boolean edgesNeedsUpdate = true;

    public Trace() {
        this.id = ++idGenerator;
    }

    @Override
    public Iterator<ILocation> iterator() {
        return locations.iterator();
    }

//	@Override
//	public ListIterator<AggConnection> connListIterator() {
//		if (connsNeedsUpdate)
//			updateConns();
//
//		return conns().listIterator();
//	}
//
//	@Override
//	public ListIterator<AggConnection> connListIterator(ILocation start) {
//		if (connsNeedsUpdate)
//			updateConns();
//
//		ListIterator<AggConnection> it = connListIterator();
//		while (it.hasNext()) {
//			if (it.next().getFrom().compareTo((AggNode) start) == 0) {
//				it.previous();
//				return it;
//			}
//		}
//		return Collections.emptyListIterator();
//	}
//
//	@Override
//	public ArrayList<AggConnection> conns() {
//		return conns;
//	}
//
//	@Override
//	public ArrayList<ILocation> connLocations() {
//		return locations;
//	}
//
//	@Override
//	public ITrace connSubTrace(ILocation start, ILocation stop) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean connIsEmpty() {
//		return locations == null || locations.size() < 2;
//	}
    @Override
    public String name() {
        return name;
    }

    @Override
    public GPSRegion getGPSRegion() {
        // TODO Auto-generated method stub
        return null;
    }

//	@Override
//	public ILocation getConnFirstLocation() {
//		return locations.get(0);
//	}
//
//	@Override
//	public ILocation getConnLastLocation() {
//		return locations.get(locations.size() - 1);
//	}
//
//	@Override
//	public void insertConnLocation(int index, ILocation location) {
//		locations.add(index, location);
//		connsNeedsUpdate = true;
//	}
//	protected void updateConns() {
//		if (!connsNeedsUpdate)
//			return;
//		conns.clear();
//
//		ILocation from = null;
//		for (ILocation l : locations) {
//			if (from == null) {
//				from = l;
//				continue;
//			}
//
//			ILocation to = l;
//			AggConnection conn =
//					new AggConnection(new AggNode(from, aggContainer), new AggNode(to, aggContainer), aggContainer);
//			conns.add(conn);
//			from = to;
//		}
//		connsNeedsUpdate = false;
//	}
    protected void updateEdges() {
        if (!edgesNeedsUpdate) {
            return;
        }
        edges.clear();

        ILocation from = null;
        for (ILocation l : locations) {
            if (from == null) {
                from = l;
                continue;
            }

            ILocation to = l;
            GPSEdge edge = new GPSEdge();
            edge.setFrom((GPSPoint) from);
            edge.setTo((GPSPoint) to);
            edges.add(edge);
            from = to;
        }
        edgesNeedsUpdate = false;
    }

    @Override
    public ListIterator<GPSEdge> edgeListIterator(ILocation start) {
        if (edgesNeedsUpdate) {
            updateEdges();
        }

        ListIterator<GPSEdge> it = edgeListIterator();
        while (it.hasNext()) {
            if (it.next().getFrom().compareTo((GPSPoint) start) == 0) {
                it.previous();
                return it;
            }
        }
        return Collections.emptyListIterator();
    }

    @Override
    public ListIterator<GPSEdge> edgeListIterator() {
        if (edgesNeedsUpdate) {
            updateEdges();
        }

        return edges().listIterator();
    }

    @Override
    public ArrayList<GPSEdge> edges() {
        return edges;
    }

    @Override
    public ArrayList<ILocation> edgeLocations() {
        return locations;
    }

    @Override
    public ITrace edgeSubTrace(ILocation start, ILocation stop) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean edgeIsEmpty() {
        return locations == null || locations.size() < 2;
    }

    @Override
    public ILocation getEdgeFirstLocation() {
        return locations.get(0);
    }

    @Override
    public ILocation getEdgeLastLocation() {
        return locations.get(locations.size() - 1);
    }

    @Override
    public void insertEdgeLocation(int index, ILocation location) {
        locations.add(index, location);
        edgesNeedsUpdate = true;
    }
}
