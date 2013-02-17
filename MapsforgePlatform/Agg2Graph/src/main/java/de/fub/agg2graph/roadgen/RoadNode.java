package de.fub.agg2graph.roadgen;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;

/**
 * A node as part of a road. Can be either an {@link Intersection} or a support
 * node for a {@link Road} object.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class RoadNode extends GPSPoint {
	public RoadNode(ILocation location) {
		super(location);
	}
}
