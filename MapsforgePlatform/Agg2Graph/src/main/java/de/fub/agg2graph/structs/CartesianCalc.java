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
package de.fub.agg2graph.structs;

import java.util.List;
import org.jscience.mathematics.vector.Float64Vector;

/**
 * Static methods for calculations on projected coordinates.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class CartesianCalc {

	public static double getDistancePointToPoint(ILocation a, ILocation b) {
		double result = Math.sqrt(Math.pow((a.getX() - b.getX()), 2)
				+ Math.pow((a.getY() - b.getY()), 2));
		return result;
	}

	public static double getDistancePointToLine(ILocation point,
			ILocation start, ILocation end) {
		// System.out.println("PointToLine " + point + ", " + start + "-" +
		// end);
		double dist = Math.abs((point.getX() - start.getX())
				* (end.getY() - start.getY()) - (point.getY() - start.getY())
				* (end.getX() - start.getX()))
				/ Math.sqrt((end.getX() - start.getX())
						* (end.getX() - start.getX())
						+ (end.getY() - start.getY())
						* (end.getY() - start.getY()));
		// double lineLength = getDistancePointToPoint(start, end);
		double angleWithStart = getAngleBetweenLines(start, point, start, end);
		double angleWithEnd = getAngleBetweenLines(point, end, start, end);
		if ((angleWithStart > 90 && angleWithEnd < 90)
				|| (angleWithStart < 90 && angleWithEnd > 90)) {
			double pointToA = getDistancePointToPoint(start, point);
			double pointToB = getDistancePointToPoint(end, point);
			return Math.min(pointToA, pointToB);
		}
		return dist;
	}

	public static double[] distancePointToTrace(ILocation point,
			List<? extends ILocation> list) {
		int minPos = 0;
		double distHere = 0;
		double minDist = Double.MAX_VALUE;
		if (list.size() == 1) {
			return new double[] { getDistancePointToPoint(point, list.get(0)),
					0 };
		}
		for (int j = minPos; j < list.size() - 1; j++) {
			distHere = CartesianCalc.getDistancePointToLine(point, list.get(j),
					list.get(j + 1));
			if (distHere < minDist) {
				minDist = distHere;
				minPos = j;
			}
			if (Double.isNaN(minDist)) {
				minDist = Double.MAX_VALUE;
			}
		}
		return new double[] { minDist, minPos };
	}

	public static double getAngleBetweenLines(ILocation pointA1,
			ILocation pointA2, ILocation pointB1, ILocation pointB2) {
		Float64Vector vecA = getVector(pointA1, pointA2);
		Float64Vector vecB = getVector(pointB1, pointB2);
		return Math.toDegrees(Math.acos((vecA.times(vecB).divide(vecA
				.normValue() * vecB.normValue())).doubleValue()));
	}

	public static ILocation getProjectionPoint(ILocation point,
			ILocation start, ILocation end) {
		ILocation result = new GPSPoint();
		Float64Vector w = getVector(new XYPoint(0, 0), point);
		Float64Vector a = getVector(start, end);
		Float64Vector proj = a.times((w.times(a).divide(Math.pow(a.normValue(),
				2))));
		result.setX(proj.get(0).doubleValue());
		result.setY(proj.get(1).doubleValue());
		return result;
	}

	public static Float64Vector getVector(ILocation a) {
		GPSPoint zero = new GPSPoint(0, 0);
		return getVector(zero, a);
	}

	public static Float64Vector getVector(ILocation a, ILocation b) {
		// System.out.println(b.getX() - a.getX());
		// System.out.println(b.getY() - a.getY());
		// System.out.println(Float64Vector.valueOf(b.getX() - a.getX(),
		// b.getY()
		// - a.getY()));
		return Float64Vector.valueOf(b.getX() - a.getX(), b.getY() - a.getY());
	}

	public static boolean isAngleMax(double angleValue, double limit) {
		return !Double.isNaN(angleValue)
				&& ((angleValue <= limit) || (angleValue >= 360 - limit));
	}

	/**
	 * Euklidean distance.
	 * 
	 * @return
	 */
	public static double getDistance(ILocation a, ILocation b) {
		double deltaLat = a.getLat() - b.getLat();
		double deltaLon = a.getLon() - b.getLon();

		return Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon);
	}

}
