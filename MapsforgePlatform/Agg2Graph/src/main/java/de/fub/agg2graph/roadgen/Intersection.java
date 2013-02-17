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

import de.fub.agg2graph.agg.AggNode;
import java.util.HashSet;
import java.util.Set;

/**
 * An intersection in the {@link RoadNetwork} which can be either a real
 * intersection or a pseudo intersection (dead-end road).
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class Intersection extends RoadNode {
	public Set<Road> out = new HashSet<Road>();
	public Set<Road> in = new HashSet<Road>();
	public AggNode baseNode;
	public Intersection mergedTo = null;

	public Intersection(AggNode baseNode) {
		super(baseNode);
		this.baseNode = baseNode;
		if (baseNode != null) {
			baseNode.setIntersection(this);
		}
	}

	@Override
	public String toString() {
		if (getID() != null) {
			return String.format("intersection %s\n", getID());
		} else {
			return String.format("intersection at [%.7f, %.7f]\n", getLat(),
					getLon());
		}
	}

	@Override
	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		if (getID() != null) {
			sb.append(String.format("intersection %s\n", getID()));
		} else {
			sb.append(String.format("intersection at [%.7f, %.7f]\n", getLat(),
					getLon()));
		}
		for (Road r : out) {
			sb.append("\t")
					.append(String.format("road to %s (%d points)", r.getTo()
							.getID(), r.path.size())).append("\n");
		}
		for (Road r : in) {
			sb.append("\t")
					.append(String.format("road from %s (%d points)", r.getTo()
							.getID(), r.path.size())).append("\n");
		}
		return sb.toString();
	}

	public boolean isPseudo() {
		if (in.size() == 0 || out.size() == 0) {
			return true;
		}
		return false;
	}

	public int getVisibleEdgeCount() {
		int sum = 0;
		for (Road r : in) {
			if (r.isVisible()) {
				sum++;
			}
		}
		for (Road r : out) {
			if (r.isVisible()) {
				sum++;
			}
		}
		return sum;
	}

	public boolean isDirectlyConnectedTo(Intersection i2) {
		for (Road r : in) {
			if (r.isVisible() && r.getFrom().equals(i2)) {
				return true;
			}
		}
		for (Road r : out) {
			if (r.isVisible() && r.getTo().equals(i2)) {
				return true;
			}
		}
		return false;
	}
}
