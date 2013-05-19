/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.mapmatcher;

import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

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

    public static class Factory {

        private static final Object MUTEX = new Object();

        public static MapMatcher getDefault() {
            MapMatcher mapMatcher = null;
            try {
                mapMatcher = find(PointToPointMapMatcher.class.getName());
            } catch (MapMatcherNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            return mapMatcher;
        }

        public static Collection<? extends MapMatcher> findAll() {
            Set<Class<? extends MapMatcher>> allClasses = Lookup.getDefault().lookupResult(MapMatcher.class).allClasses();
            List<MapMatcher> instances = new ArrayList<MapMatcher>(allClasses.size());
            for (Class<? extends MapMatcher> clazz : allClasses) {
                try {
                    instances.add(clazz.newInstance());
                } catch (InstantiationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return instances;
        }

        public static MapMatcher find(String qualifiedName) throws MapMatcherNotFoundException {
            synchronized (MUTEX) {
                MapMatcher mapMatcher = null;
                for (MapMatcher matcher : findAll()) {
                    if (matcher.getClass().getName().equals(qualifiedName)) {
                        mapMatcher = matcher;
                        break;
                    }
                }
                if (mapMatcher == null) {
                    throw new MapMatcherNotFoundException(MessageFormat.format("Couldn'T find instance {0}! Please check whether the type is registered via @ServiceProvider annotation.", qualifiedName));
                }
                return mapMatcher;
            }
        }
    }

    public static class MapMatcherNotFoundException extends Exception {

        private static final long serialVersionUID = 1L;

        public MapMatcherNotFoundException() {
        }

        public MapMatcherNotFoundException(String message) {
            super(message);
        }

        public MapMatcherNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public MapMatcherNotFoundException(Throwable cause) {
            super(cause);
        }
    }

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
