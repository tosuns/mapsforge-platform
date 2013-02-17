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
package de.fub.agg2graph.agg;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;

/**
 * Models a pair of a Point and its GhostPoint.
 * 
 * @author Johannes Mitlmeier
 */
public class PointGhostPointPair {
	public ILocation point;
	public ILocation ghostPoint;
	public AggConnection targetConnection;
	public int targetStartIndex;
	public boolean removable = false;

	private PointGhostPointPair() {

	}

	public static PointGhostPointPair createAggToTrace(AggNode original,
			GPSPoint ghost, int startIndex) {
		return createAggToTrace(original, ghost, startIndex, false);
	}

	public static PointGhostPointPair createAggToTrace(AggNode original,
			GPSPoint ghost, int startIndex, boolean removable) {
		PointGhostPointPair result = new PointGhostPointPair();
		result.point = original;
		result.ghostPoint = ghost;
		result.targetStartIndex = startIndex;
		result.removable = removable;
		return result;
	}

	public static PointGhostPointPair createTraceToAgg(GPSPoint original,
			AggNode ghost, AggConnection targetConnection) {
		return createTraceToAgg(original, ghost, targetConnection, false);
	}

	public static PointGhostPointPair createTraceToAgg(GPSPoint original,
			AggNode ghost, AggConnection targetConnection, boolean removable) {
		PointGhostPointPair result = new PointGhostPointPair();
		result.point = original;
		result.ghostPoint = ghost;
		result.targetConnection = targetConnection;
		result.removable = removable;
		return result;
	}

	public ILocation getPoint() {
		return point;
	}

	public void setPoint(ILocation point) {
		this.point = point;
	}

	public ILocation getGhostPoint() {
		return ghostPoint;
	}

	public void setGhostPoint(ILocation ghostPoint) {
		this.ghostPoint = ghostPoint;
	}

	public AggNode getAggNode() {
		if (point.getClass().getName().endsWith("AggNode")) {
			return (AggNode) point;
		}
		if (ghostPoint.getClass().getName().endsWith("AggNode")) {
			return (AggNode) ghostPoint;
		}
		return null;
	}

	public GPSPoint getGPSPoint() {
		if (point.getClass().getName().endsWith("GPSPoint")) {
			return (GPSPoint) point;
		}
		if (ghostPoint.getClass().getName().endsWith("GPSPoint")) {
			return (GPSPoint) ghostPoint;
		}
		return null;
	}

	@Override
	public String toString() {
		return point + " with ghost @ " + ghostPoint;
	}
}
