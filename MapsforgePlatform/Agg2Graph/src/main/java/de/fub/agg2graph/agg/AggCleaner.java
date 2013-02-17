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

import de.fub.agg2graph.input.CleaningOptions;
import de.fub.agg2graph.structs.CartesianCalc;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.ILocation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class for cleaning a path in the {@link AggContainer} using give
 * {@link CleaningOptions}.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class AggCleaner {
	private static Logger logger = Logger.getLogger("agg2graph.clean");

	private CleaningOptions co = new CleaningOptions();

	public AggCleaner enableDefault() {
		co.filterBySegmentLength = false;
		co.filterByEdgeLength = true;
		co.filterZigzag = true;
		co.filterFakeCircle = true;
		co.filterOutliers = true;
		return this;
	}

	public void clean(List<? extends ILocation> pointList) {
		List<ArrayList<ILocation>> result = new ArrayList<ArrayList<ILocation>>();
		ArrayList<ILocation> currentSegment = new ArrayList<ILocation>();

		for (int i = 0; i < pointList.size(); i++) {
			ILocation point = pointList.get(i);
			logger.fine("Examining point " + point);

			// check edge length
			if (co.filterByEdgeLength && currentSegment.size() > 0) {
				ILocation lastPoint = currentSegment
						.get(currentSegment.size() - 1);
				double distance = GPSCalc.getDistance(lastPoint, point);
				if (distance < co.minEdgeLength) {
					logger.fine(String.format(
							"edge length %s to %s: %.2f < %.2f", lastPoint,
							point, distance, co.minEdgeLength));
					logger.fine("short edge, dropping point " + point);
					continue;
				}
				if (distance > co.maxEdgeLength) {
					logger.fine(String.format(
							"edge length %s to %s: %.2f > %.2f", lastPoint,
							point, distance, co.maxEdgeLength));
					logger.fine("long edge, still NOT dropping point " + point);
					// make new segment
					if (currentSegment.size() > co.minSegmentLength) {
						result.add(currentSegment);
					}
					currentSegment = new ArrayList<ILocation>();
				}
			}

			double angleHere = Double.MAX_VALUE;
			double angleBefore = Double.MAX_VALUE;
			// check zigzag
			if (co.filterZigzag) {
				if (currentSegment.size() > 1 && i < pointList.size() - 1) {
					ILocation nextToLastPoint = currentSegment
							.get(currentSegment.size() - 2);
					ILocation lastPoint = currentSegment.get(currentSegment
							.size() - 1);
					ILocation nextPoint = pointList.get(i + 1);

					angleHere = CartesianCalc.getAngleBetweenLines(lastPoint,
							point, point, nextPoint);
					angleBefore = CartesianCalc.getAngleBetweenLines(
							nextToLastPoint, lastPoint, lastPoint, point);
					if (!Double.isNaN(angleHere) && !Double.isNaN(angleBefore)) {
						// is it zigzagged?
						if (((angleHere > 180 - co.maxZigzagAngle) && (angleBefore < co.maxZigzagAngle))
								|| ((angleHere < co.maxZigzagAngle) && (angleBefore > 180 - co.maxZigzagAngle))) {
							logger.fine("found zigzag");
							logger.fine(String.format("%.3f <- -> %.3f",
									angleBefore, angleHere));
							i++;
							continue;
						}
					} else {
						logger.fine("something bad");
					}
				}
			}

			// check fake circles
			if (co.filterFakeCircle) {
				if (currentSegment.size() > 1 && i < pointList.size() - 1) {
					if (!co.filterZigzag) {
						ILocation nextToLastPoint = currentSegment
								.get(currentSegment.size() - 2);
						ILocation lastPoint = currentSegment.get(currentSegment
								.size() - 1);
						ILocation nextPoint = pointList.get(i + 1);

						angleHere = CartesianCalc.getAngleBetweenLines(
								lastPoint, point, point, nextPoint);
						angleBefore = CartesianCalc.getAngleBetweenLines(
								nextToLastPoint, lastPoint, lastPoint, point);
					}

					if (!Double.isNaN(angleHere) && !Double.isNaN(angleBefore)) {
						// is it a fake circle?
						if ((angleHere > 180 - co.maxFakeCircleAngle)
								&& (angleBefore > 180 - co.maxFakeCircleAngle)) {
							logger.fine("found fake circle");
							logger.fine(String.format("%.3f <- -> %.3f",
									angleBefore, angleHere));
							// insert as second but last element
							ILocation oldLastPoint = currentSegment
									.remove(currentSegment.size() - 1);
							currentSegment.add(point);
							currentSegment.add(oldLastPoint);
							i++;
							continue;
						}
					}
				}
			}

			// segment length (split if necessary)
			if (co.filterBySegmentLength
					&& currentSegment.size() >= co.maxSegmentLength) {
				// make new segment
				if (currentSegment.size() > co.minSegmentLength) {
					result.add(currentSegment);
				}
				currentSegment = new ArrayList<ILocation>();
			}

			logger.fine("adding point " + point);
			currentSegment.add(point);
		}

		// save last segment
		if (currentSegment.size() > co.minSegmentLength) {
			result.add(currentSegment);
		}
	}
}
