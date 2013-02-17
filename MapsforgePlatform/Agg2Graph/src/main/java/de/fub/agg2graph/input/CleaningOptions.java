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
package de.fub.agg2graph.input;

import de.fub.agg2graph.agg.AggCleaner;

/**
 * Container class for several options to be used with {@link GPSCleaner} or
 * {@link AggCleaner}.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class CleaningOptions {
	// number of edges allowed per segment
	public boolean filterBySegmentLength = false;
	public long minSegmentLength = 1;
	public long maxSegmentLength = Long.MAX_VALUE;
	// min/max distance between two points (meters)
	public boolean filterByEdgeLength = true;
	public double minEdgeLength = 0.1;
	public double maxEdgeLength = 500;
	// length change between two consecutive edges
	public boolean filterByEdgeLengthIncrease = true;
	public double minEdgeLengthIncreaseFactor = 10;
	public double minEdgeLengthAfterIncrease = 30;
	// zigzag
	public boolean filterZigzag = true;
	public double maxZigzagAngle = 30;
	// fake circle
	public boolean filterFakeCircle = true;
	public double maxFakeCircleAngle = 50;
	// outliers
	public boolean filterOutliers = true;
	public int maxNumOutliers = 2;

}
