/**
 * *****************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the GNU
 * Affero Public License v3.0 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Contributors: Johannes Mitlmeier - initial API and implementation
 * ****************************************************************************
 */
package de.fub.agg2graph.input;

import de.fub.agg2graph.structs.BoundedQueue;
import de.fub.agg2graph.structs.CartesianCalc;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Prepocess GPS data by several means. See {@link CleaningOptions} for
 * supported cleaning options and their respective settings.
 *
 * @author Johannes Mitlmeier
 *
 */
public class GPSCleaner {

    private static final Logger logger = Logger.getLogger("agg2graph.clean");
    private CleaningOptions cleaningOptions = new CleaningOptions();

    public GPSCleaner enableDefault() {
        cleaningOptions.filterBySegmentLength = true;
        cleaningOptions.filterByEdgeLength = true;
        cleaningOptions.filterByEdgeLengthIncrease = true;
        cleaningOptions.filterZigzag = true;
        cleaningOptions.filterFakeCircle = true;
        cleaningOptions.filterOutliers = true;
        return this;
    }

    public List<GPSSegment> clean(GPSSegment segment) {
        List<GPSPoint> pointList = segment;
        List<GPSSegment> result = new ArrayList<GPSSegment>();
        GPSSegment currentSegment = new GPSSegment();
        BoundedQueue<Double> lastLengths = new BoundedQueue<Double>(5);

        for (int i = 0; i < pointList.size(); i++) {
            GPSPoint point = pointList.get(i);
            ILocation lastPoint = currentSegment.size() > 0 ? currentSegment
                    .get(currentSegment.size() - 1) : null;
            logger.log(Level.FINE, "Examining point {0}", point);

            double length = 0;
            if (cleaningOptions.filterByEdgeLength || cleaningOptions.filterByEdgeLengthIncrease) {
                if (lastPoint != null) {
                    length = GPSCalc.getDistance(lastPoint, point);
                    lastLengths.offer(length);
                }
            }
            // check edge length
            if (cleaningOptions.filterByEdgeLength && currentSegment.size() > 0) {
                if (length < cleaningOptions.minEdgeLength) {
                    logger.fine(String.format(
                            "edge length %s to %s: %.2f < %.2f", lastPoint,
                            point, length, cleaningOptions.minEdgeLength));
                    logger.log(Level.FINE, "short edge, dropping point {0}", point);
                    continue;
                }
                if (length > cleaningOptions.maxEdgeLength) {
                    logger.fine(String.format(
                            "edge length %s to %s: %.2f > %.2f", lastPoint,
                            point, length, cleaningOptions.maxEdgeLength));
                    logger.log(Level.FINE, "long edge, still NOT dropping point {0}", point);
                    // make new segment
                    if (currentSegment.size() > cleaningOptions.minSegmentLength) {
                        result.add(currentSegment);
                    }
                    currentSegment = new GPSSegment();
                }
            }

            // check increase of edge length
            if (cleaningOptions.filterByEdgeLengthIncrease) {
                if (lastLengths.size() > 1) {
                    if (length > cleaningOptions.minEdgeLengthAfterIncrease
                            && length / lastLengths.get(lastLengths.size() - 2) > cleaningOptions.minEdgeLengthIncreaseFactor) {
                        // make new segment
                        if (currentSegment.size() > cleaningOptions.minSegmentLength) {
                            result.add(currentSegment);
                        }
                        currentSegment = new GPSSegment();
                    }
                }
            }

            double angleHere = Double.MAX_VALUE;
            double angleBefore = Double.MAX_VALUE;
            // check zigzag
            if (cleaningOptions.filterZigzag) {
                if (currentSegment.size() > 1 && i < pointList.size() - 1) {
                    GPSPoint nextToLastPoint = currentSegment
                            .get(currentSegment.size() - 2);
                    GPSPoint nextPoint = pointList.get(i + 1);

                    angleHere = CartesianCalc.getAngleBetweenLines(lastPoint,
                            point, point, nextPoint);
                    angleBefore = CartesianCalc.getAngleBetweenLines(
                            nextToLastPoint, lastPoint, lastPoint, point);
                    if (!Double.isNaN(angleHere) && !Double.isNaN(angleBefore)) {
                        // is it zigzagged?
                        if (((angleHere > 180 - cleaningOptions.maxZigzagAngle) && (angleBefore < cleaningOptions.maxZigzagAngle))
                                || ((angleHere < cleaningOptions.maxZigzagAngle) && (angleBefore > 180 - cleaningOptions.maxZigzagAngle))) {
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
            if (cleaningOptions.filterFakeCircle) {
                if (currentSegment.size() > 1 && i < pointList.size() - 1) {
                    if (!cleaningOptions.filterZigzag) {
                        GPSPoint nextToLastPoint = currentSegment
                                .get(currentSegment.size() - 2);
                        GPSPoint nextPoint = pointList.get(i + 1);

                        angleHere = CartesianCalc.getAngleBetweenLines(
                                lastPoint, point, point, nextPoint);
                        angleBefore = CartesianCalc.getAngleBetweenLines(
                                nextToLastPoint, lastPoint, lastPoint, point);
                    }

                    if (!Double.isNaN(angleHere) && !Double.isNaN(angleBefore)) {
                        // is it a fake circle?
                        if ((angleHere > 180 - cleaningOptions.maxFakeCircleAngle)
                                && (angleBefore > 180 - cleaningOptions.maxFakeCircleAngle)) {
                            logger.fine("found fake circle");
                            logger.fine(String.format("%.3f <- -> %.3f",
                                    angleBefore, angleHere));
                            // insert as second but last element
                            GPSPoint oldLastPoint = currentSegment
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
            if (cleaningOptions.filterBySegmentLength
                    && currentSegment.size() >= cleaningOptions.maxSegmentLength) {
                // make new segment
                if (currentSegment.size() > cleaningOptions.minSegmentLength) {
                    result.add(currentSegment);
                }
                currentSegment = new GPSSegment();
            }

            logger.log(Level.FINE, "adding point {0}", point);
            currentSegment.add(point);
        }

        // save last segment
        if (!cleaningOptions.filterBySegmentLength
                || currentSegment.size() > cleaningOptions.minSegmentLength) {
            result.add(currentSegment);
        }
        return result;
    }

    public CleaningOptions getCleaningOptions() {
        return cleaningOptions;
    }

    public void setCleaningOptions(CleaningOptions options) {
        cleaningOptions = options;
    }
}
