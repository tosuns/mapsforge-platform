package de.fub.agg2graph.structs.frechet;

import java.util.ArrayList;
import java.util.ListIterator;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSRegion;
import de.fub.agg2graph.structs.ILocation;

public interface ITrace extends Iterable<ILocation> {
	
//	ListIterator<AggConnection> connListIterator();
	ListIterator<GPSEdge> edgeListIterator(ILocation start);

//	ListIterator<AggConnection> connListIterator(ILocation start);
	ListIterator<GPSEdge> edgeListIterator();
	
//	ArrayList<AggConnection> conns();	
	ArrayList<GPSEdge> edges();
	
//	ArrayList<ILocation> connLocations();
	ArrayList<ILocation> edgeLocations();
	
//	ITrace connSubTrace(ILocation start, ILocation stop);
	ITrace edgeSubTrace(ILocation start, ILocation stop);
	
//	boolean connIsEmpty();
	boolean edgeIsEmpty();
	
	String name();
	GPSRegion getGPSRegion();
	
//	ILocation getConnFirstLocation();
	ILocation getEdgeFirstLocation();
	
//	ILocation getConnLastLocation();
	ILocation getEdgeLastLocation();
	
//	void insertConnLocation(int index, ILocation location);
	void insertEdgeLocation(int index, ILocation location);
}

