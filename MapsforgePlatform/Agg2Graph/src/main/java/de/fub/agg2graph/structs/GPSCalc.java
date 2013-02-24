/**
 * *****************************************************************************
 * Copyright 2013 Johannes Mitlmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
package de.fub.agg2graph.structs;

import de.fub.agg2graph.utils.MathUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.Float64Vector;

/**
 * Static methods for calculations on gps data like distance and angles.
 *
 * @author Johannes Mitlmeier
 *
 */
public class GPSCalc {

    /**
     * in meters
     *
     * @param a
     * @param b
     * @return
     */
    public static double getDistance(double lat1, double lon1, double lat2,
            double lon2) {
        return getSimpleDistance(lat1, lon1, lat2, lon2);
        //  former use of jcoord library removed because of licensing incompatibilities
        //	return new LatLng(lat1, lon1).distance(new LatLng(lat2, lon2)) * 1000;
    }

    public static double getDistance(ILocation a, ILocation b) {
        double result = getDistance(a.getLat(), a.getLon(), b.getLat(),
                b.getLon());
        if (Double.isNaN(result)) {
            if (a.getLat() == b.getLat() && a.getLon() == b.getLon()) {
                return 0.0;
            }
            return Double.NaN;
        }
        return result;
    }

    public static double getSimpleDistance(double lat1, double lon1,
            double lat2, double lon2) {
        double lat = (lat1 + lat2) / 2 * 0.01745;
        double dx = 111.3 * Math.cos(lat) * (lon1 - lon2);
        double dy = 111.3 * (lat1 - lat2);
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance * 1000;
    }

    public static double getPreciseDistance(double lat1, double lon1,
            double lat2, double lon2) {

        double lat = new BigDecimal(lat1 + lat2).divide(new BigDecimal(2), RoundingMode.HALF_UP).doubleValue() * 0.01745;
        double dx = 111.3 * Math.cos(lat) * (lon1 - lon2);
        double dy = 111.3 * (lat1 - lat2);
        double distance = MathUtil.sqrt(new BigDecimal(dx * dx + dy * dy), RoundingMode.HALF_UP).doubleValue();
        return distance * 1000;
    }

    public static double getSimpleDistance(ILocation a, ILocation b) {
        return getSimpleDistance(a.getLat(), a.getLon(), b.getLat(), b.getLon());
    }

    /**
     * Only usable for very short distances.
     *
     * @param a
     * @param b
     * @return
     */
    public static ILocation getMidwayLocation(ILocation a, ILocation b) {
        BigDecimal TWO = new BigDecimal(2);
        double newLat = a.getLat() + new BigDecimal(b.getLat() - a.getLat()).divide(TWO, RoundingMode.HALF_UP).doubleValue();
        double newLon = a.getLon() + new BigDecimal(b.getLon() - a.getLon()).divide(TWO, RoundingMode.HALF_UP).doubleValue();
        return new GPSPoint(newLat, newLon);
    }

    public static double getGradient(double lat1, double lon1, double lat2,
            double lon2) {
        // m = (delta_y / delta_x)

        // border cases
        if (Math.abs(lon2 - lon1) < 10e-4) {
            if (lat2 - lat1 == 0) {
                return 0;
            } else if (lat2 - lat1 > 0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }
        return (lat2 - lat1) / (lon2 - lon1);
    }

    public static double getGradient(ILocation a, ILocation b) {
        return getGradient(a.getLat(), a.getLon(), b.getLat(), b.getLon());
    }

    public static double getDistancePointToEdge(ILocation point,
            ILocation start, ILocation end) {
        // project to edge and get distance to projection
        ILocation projection = getProjectionPoint(point, start, end);
        if (projection != null) {
            return getDistance(point, projection);
        }

        double pointToA = getDistance(start, point);
        double pointToB = getDistance(end, point);
        return Math.min(pointToA, pointToB);
    }

    public static boolean isDistancePointToTraceBelowLimit(ILocation point,
            List<? extends ILocation> list, double limit) {
        double distHere = 0;
        if (list.size() == 1) {
            return getDistance(point, list.get(0)) < limit;
        }
        for (int j = 0; j < list.size() - 1; j++) {
            distHere = GPSCalc.getDistancePointToEdge(point, list.get(j),
                    list.get(j + 1));
            if (distHere < limit) {
                return true;
            }
        }
        return false;
    }

    public static double[] distancePointToTrace(ILocation point,
            List<? extends ILocation> list) {
        int minPos = 0;
        double distHere = 0;
        double minDist = Double.MAX_VALUE;
        if (list.size() == 1) {
            return new double[]{getDistance(point, list.get(0)), 0};
        }
        for (int j = minPos; j < list.size() - 1; j++) {
            distHere = GPSCalc.getDistancePointToEdge(point, list.get(j),
                    list.get(j + 1));
            if (distHere < minDist) {
                minDist = distHere;
                minPos = j;
            }
            if (Double.isNaN(minDist)) {
                minDist = Double.MAX_VALUE;
            }
        }
        return new double[]{minDist, minPos};
    }

    public static double getAngleBetweenEdges(ILocation pointA1,
            ILocation pointA2, ILocation pointB1, ILocation pointB2) {
        Float64Vector vecA = getVector(pointA1, pointA2);
        Float64Vector vecB = getVector(pointB1, pointB2);
        return Math.toDegrees(Math.acos((vecA.times(vecB).divide(vecA
                .normValue() * vecB.normValue())).doubleValue()));
    }

    public static double getAngleBetweenEdges(IEdge<? extends ILocation> edge1,
            IEdge<? extends ILocation> edge2) {
        return getAngleBetweenEdges(edge1.getFrom(), edge1.getTo(),
                edge2.getFrom(), edge2.getTo());
    }

    public static double getSmallGradientFromEdges(
            IEdge<? extends ILocation> edgeA, IEdge<? extends ILocation> edgeB) {
        return getSmallGradientFromEdges(edgeA.getFrom(), edgeA.getTo(),
                edgeB.getFrom(), edgeB.getTo());
    }

    /**
     * This method calculates a mismatch in two gradients.
     *
     * @param pointA1
     * @param pointA2
     * @param pointB1
     * @param pointB2
     * @return 0 for identical gradients, 2 for opposite gradients and in
     * between.
     * @author Sebastian Müller
     */
    public static double getSmallGradientFromEdges(ILocation pointA1,
            ILocation pointA2, ILocation pointB1, ILocation pointB2) {
        double mA = Double.MAX_VALUE;
        boolean diff1 = false;
        boolean diff2 = false;
        if (!(pointA1.getY() == pointA2.getY())) {
            double d1 = pointA2.getX() - pointA1.getX();
            double d2 = pointA2.getY() - pointA1.getY();
            mA = d2 / d1;
            if (d1 < 0 && d2 < 0) {
                diff1 = true;
            }
            if (d1 < 0 && d2 > 0) {
                diff2 = true;
            }
        }
        double mB = Double.MAX_VALUE;
        if (!(pointB1.getY() == pointB2.getY())) {
            double d1 = pointB2.getX() - pointB1.getX();
            double d2 = pointB2.getY() - pointB1.getY();
            mB = d2 / d1;
            if (d1 < 0 && d2 < 0 && diff1 == true) {
                diff1 = false;
            } else if (d1 < 0 && d2 < 0 && diff1 == false) {
                diff1 = true;
            }
            if (d1 < 0 && d2 > 0 && diff2 == true) {
                diff2 = false;
            } else if (d1 < 0 && d2 > 0 && diff2 == false) {
                diff2 = true;
            }
        }

        if (mA > 1 || mA < -1) {
            mA = (2 - (1 / Math.abs(mA))) * Math.signum(mA);
        }
        if (mB > 1 || mB < -1) {
            mB = (2 - (1 / Math.abs(mB))) * Math.signum(mB);
        }

        if (Math.signum(mA) != Math.signum(mB) && diff1 == diff2) {
            return Math.abs(mA) + Math.abs(mB);
        } else if (Math.signum(mA) == Math.signum(mB) && !diff1 && !diff2) {
            return Math.abs(mA - mB);
        } else if (Math.signum(mA) == Math.signum(mB) && (diff1 || diff2)) {
            return 4 - Math.abs(mA - mB);
        } else {
            return 4 - (Math.abs(mA) + Math.abs(mB));
        }

    }

    public static ILocation getPointAverage(List<ILocation> locations) {
        if (locations.isEmpty()) {
            return null;
        }
        GPSPoint zero = new GPSPoint(0, 0);
        Float64Vector sum = getVector(zero);
        for (ILocation point : locations) {
            sum = sum.plus(getVector(point));
        }
        Float64Vector result = sum.times(1.0 / locations.size());
        return new GPSPoint(result.getValue(0), result.getValue(1));
    }

    public static Float64Vector getVector(ILocation a) {
        GPSPoint zero = new GPSPoint(0, 0);
        return getVector(zero, a);
    }

    public static Float64Vector getVector(ILocation a, ILocation b) {
        return Float64Vector.valueOf(b.getLat() - a.getLat(),
                b.getLon() - a.getLon());
    }

    public static double getDistancePointToEdge(ILocation point,
            IEdge<? extends ILocation> edge) {
        return getDistancePointToEdge(point, edge.getFrom(), edge.getTo());
    }

    public static ILocation getProjectionPoint(ILocation point,
            ILocation start, ILocation end) {
        ILocation result = new GPSPoint();
        Float64Vector w = getVector(start, point);
        Float64Vector a = getVector(start, end);
        Float64 factor = (w.times(a).divide(Math.pow(a.normValue(), 2)));
        Float64Vector proj = getVector(new GPSPoint(0, 0), start).plus(
                a.times(factor));
        if (!((proj.get(0).doubleValue() >= start.getLat() && proj.get(0)
                .doubleValue() <= end.getLat()) || (proj.get(0).doubleValue() <= start
                .getLat() && proj.get(0).doubleValue() >= end.getLat()))) {
            return null;
        }
        if (!((proj.get(1).doubleValue() >= start.getLon() && proj.get(1)
                .doubleValue() <= end.getLon()) || (proj.get(1).doubleValue() <= start
                .getLon() && proj.get(1).doubleValue() >= end.getLon()))) {
            return null;
        }
        result.setLat(proj.get(0).doubleValue());
        result.setLon(proj.get(1).doubleValue());
        return result;
    }

    public static ILocation getProjectionPoint(ILocation point,
            IEdge<? extends ILocation> edge) {
        return getProjectionPoint(point, edge.getFrom(), edge.getTo());
    }
}
