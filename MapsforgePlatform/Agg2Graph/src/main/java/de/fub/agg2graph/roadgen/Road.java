/*******************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Affero Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/agpl-3.0.html
 * 
 * Contributors:
 *     Johannes Mitlmeier - initial API and implementation
 ******************************************************************************/
package de.fub.agg2graph.roadgen;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.AbstractEdge;
import de.fub.agg2graph.structs.Hideable;
import de.fub.agg2graph.structs.IEdge;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.structs.Path;
import java.util.ArrayList;
import java.util.List;

public class Road extends AbstractEdge<Intersection> implements Hideable {
	public enum RoadType {
		UNKNOWN, PRIMARY, SECONDARY, TERTIARY
	}

	private boolean visible = true;
	private RoadType type = RoadType.UNKNOWN;
	public Path<AggNode> path = new Path<AggNode>();
	private boolean oneWay = true;
	public Road mergedTo = null;

	public Road() {
	}

	public Road(Intersection from, Intersection to, List<IEdge<AggNode>> path) {
		super(from, to);
		if (from == null || to == null) {
			return;
		}
		for (IEdge<AggNode> conn : path) {
			this.path.add(conn);
		}
	}

	public RoadType getType() {
		return type;
	}

	public void setType(RoadType type) {
		this.type = type;
	}

	public List<? extends ILocation> getNodes() {
		List<AggNode> pathNodes = path.getNodes();
		List<ILocation> allNodes = new ArrayList<ILocation>(
				pathNodes.size() + 2);
		if (getFrom() != null
				&& (pathNodes.size() == 0
						|| getFrom().getLat() != pathNodes.get(0).getLat() || getFrom()
						.getLon() != pathNodes.get(0).getLon())) {
			allNodes.add(getFrom());
		}
		allNodes.addAll(pathNodes);
		if (getTo() != null
				&& (pathNodes.size() == 0
						|| getTo().getLat() != pathNodes.get(
								pathNodes.size() - 1).getLat() || getTo()
						.getLon() != pathNodes.get(pathNodes.size() - 1)
						.getLon())) {
			allNodes.add(getTo());
		}
		return allNodes;
	}

	@Override
	public IEdge<Intersection> setTo(Intersection to) {
		if (path.size() > 0) {
			path.get(path.size() - 1).setTo(to.baseNode);
		}
		return super.setTo(to);
	}

	@Override
	public IEdge<Intersection> setFrom(Intersection from) {
		if (path.size() > 0) {
			path.get(0).setFrom(from.baseNode);
		}
		return super.setFrom(from);
	}

	@Override
	public double getLength() {
		if (path.size() == 0) {
			return super.getLength();
		}
		return path.getLength();
	}

	@Override
	public String toString() {
		StringBuilder stringPath = new StringBuilder();
		if (path != null && path.size() > 0) {
			AggNode loc;
			for (int i = 0; i < path.size(); i++) {
				loc = path.get(i).getFrom();
				stringPath.append(", ").append(loc);
			}
		} else {
			stringPath.append(", ");
		}
		return String.format("road from %s to %s via [%s]", from, to,
				stringPath.toString().substring(2));
	}

	public boolean isIsolated() {
		return getFrom().isPseudo() && getTo().isPseudo();
	}

	public boolean isBorderRoad() {
		return getFrom().isPseudo() ^ getTo().isPseudo(); // XOR
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public double getAvgDist() {
		double sum = 0;
		for (IEdge<AggNode> conn : path) {
			sum += ((AggConnection) conn).getAvgDist();
		}
		return sum / path.size();
	}

	public double getWeight() {
		double sum = 0;
		for (IEdge<AggNode> conn : path) {
			sum += ((AggConnection) conn).getWeight();
		}
		return sum / path.size();
	}

	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}

	public boolean isOneWay() {
		return oneWay;
	}
}
