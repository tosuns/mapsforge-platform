/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
package de.fub.agg2graph.agg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.agg2graph.agg.tiling.Tile;
import de.fub.agg2graph.roadgen.Intersection;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;

/**
 * Node in the aggregation. A node can have multiple incoming and outgoing
 * {@link AggConnection}s linking other AggNodes. It is capable of saving the
 * intersection information (how many tracks from which input to which output).
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class AggNode extends GPSPoint {
	private boolean loaded = false;
	private AggContainer aggContainer;

	private Intersection intersection;
	public Set<AggConnection> out = new HashSet<AggConnection>();
	public Set<AggConnection> in = new HashSet<AggConnection>();

	private double weight = 2;
	private Map<String, Integer> turnMap = new HashMap<String, Integer>();
	/**
	 * Invisible {@link AggNode}s might be used to hide them before generating a
	 * road network.
	 */
	private boolean visible = true;

	public AggNode(double lat, double lon, AggContainer aggContainer) {
		init(null, lat, lon, aggContainer);
	}

	public AggNode(ILocation location, AggContainer aggContainer) {
		this.aggContainer = aggContainer;
		if (location != null) {
			init(location.getID(), location.getLat(), location.getLon(),
					aggContainer);
		}
	}

	public AggNode(String ID, double lat, double lon, AggContainer aggContainer) {
		init(ID, lat, lon, aggContainer);
	}

	public AggNode(AggNode node) {
		this(node, node.aggContainer);
	}

	/**
	 * Initialize AggNode. Sets lat, lon and {@link AggContainer}.
	 * 
	 * @param ID
	 * @param lat
	 * @param lon
	 * @param aggContainer
	 */
	public void init(String ID, double lat, double lon,
			AggContainer aggContainer) {
		this.aggContainer = aggContainer;
		setID(ID);
		setLatLon(lat, lon);
	}

	/**
	 * Check if the AggNode is loaded (i.e. all {@link AggConnection}s are in
	 * memory).
	 * 
	 * @return
	 */
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public void setID(String ID) {
		this.ID = ID;
	}

	public void addIn(AggConnection inConn) {
		if (!isLoaded()) {
			// TODO load node
		}
		getIn().add(inConn);
	}

	public int getNumberOfTurnInformation() {
		int result = 0;
		for (String key : turnMap.keySet()) {
			result += turnMap.get(key);
		}
		return result;
	}

	public Map<String, Integer> getTurnMap() {
		return turnMap;
	}

	public void addTurn(AggNode before, AggNode after) {
		// keep it tracked
		String key = before.getID() + "#" + after.getID();
		// System.out.println("adding turn " + key);
		int counter = 0;
		if (turnMap.containsKey(key)) {
			counter = turnMap.get(key);
		}
		counter++;
		turnMap.put(key, counter);
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public int getTurnCount(AggNode from, AggNode to) {
		String key = from.getID() + "#" + to.getID();
		if (turnMap.containsKey(key)) {
			return turnMap.get(key);
		}
		return 0;
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public int getTurnCount(AggConnection from, AggConnection to) {
		return getTurnCount(from.getFrom(), to.getTo());
	}

	public void addIn(AggConnection inConn, AggConnection afterConn) {
		addIn(inConn);
		addTurn(inConn.getFrom(), afterConn.getTo());
	}

	public void addOut(AggConnection outConn) {
		if (!isLoaded()) {
			// TODO load node
		}
		getOut().add(outConn);
	}

	public void addOut(AggConnection outConn, AggConnection beforeConn) {
		addOut(outConn);
		addTurn(beforeConn.getFrom(), outConn.getTo());
	}

	public Set<AggConnection> getIn() {
		return in;
	}

	public Set<AggConnection> getVisibleIn() {
		if (in == null) {
			return in;
		}
		Set<AggConnection> resultSet = new HashSet<AggConnection>();
		for (Iterator<AggConnection> i = getIn().iterator(); i.hasNext();) {
			AggConnection element = i.next();
			if (element.isVisible()) {
				// i.remove();
				resultSet.add(element);
			}
		}
		return resultSet;
	}

	public Set<AggConnection> getOut() {
		return out;
	}

	public Set<AggConnection> getVisibleOut() {
		if (getOut() == null) {
			return getOut();
		}
		Set<AggConnection> resultSet = new HashSet<AggConnection>();
		for (Iterator<AggConnection> i = getOut().iterator(); i.hasNext();) {
			AggConnection element = i.next();
			if (element.isVisible()) {
				// i.remove();
				resultSet.add(element);
			}
		}
		return resultSet;
	}

	public AggContainer getAggContainer() {
		return aggContainer;
	}

	public void setAggContainer(AggContainer aggContainer) {
		this.aggContainer = aggContainer;
	}

	@Override
	public String toString() {
		if (ID != null) {
			return "{" + ID + "}";
		}
		return "AggNode [lat=" + getLat() + ", lon=" + getLon() + "]";
	}

	@Override
	public String toDebugString() {
		return "AggNode [ID=" + ID + ", lat=" + getLat() + ", lon=" + getLon()
				+ "]";
	}

	@Override
	public double getWeight() {
		return weight;
	}

	/**
	 * Update the weight of the node. This sums the weight of all incoming and
	 * outgoing {@link AggConnection}s.
	 */
	public void refreshWeight() {
		double inWeight = 0, outWeight = 0;
		// loop all connections and sum their weights
		if (getIn() == null) {
			inWeight += 1;
		} else {
			for (AggConnection conn : getIn()) {
				if (conn.isVisible()) {
					inWeight += conn.getWeight();
				}
			}
		}
		if (getOut() == null) {
			outWeight += 1;
		} else {
			for (AggConnection conn : getOut()) {
				if (conn.isVisible()) {
					outWeight += conn.getWeight();
				}
			}
		}
		weight = inWeight + outWeight;
	}

	@Override
	public int hashCode() {
		return (String.valueOf(getID()) + ":" + Arrays.toString(getLatLon()))
				.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!AggNode.class.isAssignableFrom(this.getClass())
				|| !AggNode.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		AggNode other = (AggNode) obj;
		if (ID == null) {
			if (other.ID != null) {
				return false;
			}
		} else {
			if (!ID.equals(other.ID)) {
				return false;
				// } else {
				// return true;
			}
		}
		if (latlon[0] != other.latlon[0] || latlon[1] != other.latlon[1]) {
			return false;
		}
		return true;
	}

	public String getInternalID() {
		return String.valueOf(getLat()) + ":" + String.valueOf(getLon()) + ":"
				+ ID;
	}

	/**
	 * Update latitude and longitude values. This method takes care of making
	 * the {@link ICachingStrategy} aware of the move because the {@link Tile}
	 * this AggNode belongs to might change.
	 */
	@Override
	public void setLatLon(double lat, double lon) {
		boolean moved = (this.latlon[0] != lat || this.latlon[1] != lon)
				&& (this.latlon[0] != Double.MAX_VALUE && this.latlon[1] != Double.MAX_VALUE);
		if (moved) {
			// remove from cache
			aggContainer.getCachingStrategy().removeNode(this);
		}
		super.setLatLon(lat, lon);
		if (moved) {
			// reload to cache
			aggContainer.getCachingStrategy().addNode(this);
		}
	}

	/**
	 * Update x and y values. This method takes care of making the
	 * {@link ICachingStrategy} aware of the move because the {@link Tile} this
	 * AggNode belongs to might change.
	 */
	@Override
	public void setXY(double x, double y) {
		boolean moved = (this.xy[0] != x || this.xy[1] != y)
				&& (this.xy[0] != Double.MAX_VALUE && this.xy[1] != Double.MAX_VALUE);
		if (moved) {
			// remove from cache
			aggContainer.getCachingStrategy().removeNode(this);
		}
		super.setXY(x, y);
		if (moved) {
			// reload to cache
			aggContainer.getCachingStrategy().addNode(this);
		}
	}

	/**
	 * Find the connection to an other AggNode.
	 * 
	 * @param otherNode
	 * @return Connection to otherNode, null otherwise.
	 */
	public AggConnection getConnectionTo(AggNode otherNode) {
		// TODO make sure this node is fully loaded
		for (AggConnection conn : getOut()) {
			if (conn.getTo().equals(otherNode)) {
				return conn;
			}
		}
		return null;
	}

	public boolean isShallow() {
		return false;
	}

	/**
	 * Check if this AggNode is an intersection. In this case an intersection is
	 * defined as an AggNode with more than one incoming or outgoing
	 * {@link AggConnection}.
	 * 
	 * @return
	 */
	public boolean isAggIntersection() {
		return getVisibleOut().size() > 1 || getVisibleIn().size() > 1;
	}

	/**
	 * Check if this AggNode is a node at the end of a street. This means it
	 * either has no incoming or no outgoing {@link AggConnection}s while having
	 * at least one {@link AggConnection} of the other type.
	 * 
	 * @return
	 */
	public boolean isEndNode() {
		return getVisibleOut().size() == 0 ^ getVisibleIn().size() == 0;
	}

	public Intersection getIntersection() {
		return intersection;
	}

	/**
	 * Set which intersection this {@link AggNode} belongs to.
	 * 
	 * @param intersection
	 */
	public void setIntersection(Intersection intersection) {
		this.intersection = intersection;
	}

}
