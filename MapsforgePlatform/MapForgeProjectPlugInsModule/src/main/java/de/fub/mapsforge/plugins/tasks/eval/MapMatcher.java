/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Serdar
 */
public interface MapMatcher {

    /**
     * finds the match for the specified {@code road}. The method iterates
     * through all roads in the {@code roadNetwork} and finds the most fitting
     * road. A Road can contain one or more points
     *
     * @param roadTobeMatched the segment to be matched
     * @param roadNetwork the network in which the match is to be found.
     * @return A MapMatchResult that contains one or more gps points if there
     * was a match, otherwise null.
     */
    public List<MapMatchSegment> findMatch(Collection<GPSSegment> roadTobeMatched, Collection<GPSSegment> roadNetwork);

    public static class MapMatchResult {

        private ILocation tobeMatchedPoint;
        private ILocation matchedPoint;
        private double distance;

        public MapMatchResult(ILocation tobeMatchedPoint, ILocation matchedPoint, double distance) {
            this.tobeMatchedPoint = tobeMatchedPoint;
            this.matchedPoint = matchedPoint;
            this.distance = distance;
        }

        public ILocation getTobeMatchedPoint() {
            return tobeMatchedPoint;
        }

        public ILocation getMatchedPoint() {
            return matchedPoint;
        }

        public double getDistance() {
            return distance;
        }
    }

    public static class MapMatchSegment {

        private List<MapMatchResult> segment = new ArrayList<MapMatchResult>();

        public MapMatchSegment() {
        }

        public List<MapMatchResult> getSegment() {
            return segment;
        }

        public double getMapMatchCost() {
            double cost = Double.MAX_VALUE;
            double costSum = 0;
            if (!getSegment().isEmpty()) {
                for (MapMatchResult mapMatchResult : getSegment()) {
                    costSum += mapMatchResult.getDistance();
                }
                cost = costSum / getSegment().size();
            }
            return cost;
        }
    }
}
