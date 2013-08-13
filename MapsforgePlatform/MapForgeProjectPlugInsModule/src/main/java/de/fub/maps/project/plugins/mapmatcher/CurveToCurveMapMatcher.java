/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.plugins.mapmatcher;

import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import de.fub.maps.project.plugins.tasks.eval.GpsSegmentTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = MapMatcher.class)
public class CurveToCurveMapMatcher implements MapMatcher {

    private final GpsSegmentTree TREE = new GpsSegmentTree();

    @Override
    public List<MapMatchSegment> findMatch(Collection<GPSSegment> roadTobeMatched, Collection<GPSSegment> roadNetwork) {
        TREE.reset();


        List<MapMatchSegment> resultList = new ArrayList<MapMatchSegment>(roadNetwork.size());

        HashMap<GPSSegment, List<GPSSegment>> candidateSegmentMap = findCandidateSegment(roadTobeMatched, roadNetwork);

        for (Entry<GPSSegment, List<GPSSegment>> entry : candidateSegmentMap.entrySet()) {
            GPSSegment segment = entry.getKey();
            List<GPSSegment> candidateSegments = entry.getValue();
            MapMatchSegment minMatchSegment = null;

            for (GPSSegment candidateSegment : candidateSegments) {
                MapMatchSegment mapMatchSegment = createMapMatchSegment(segment, candidateSegment);

                if (minMatchSegment == null
                        || mapMatchSegment.getMapMatchCost() < minMatchSegment.getMapMatchCost()) {
                    minMatchSegment = mapMatchSegment;
                }
            }

            if (minMatchSegment != null) {
                resultList.add(minMatchSegment);
            }
        }

        return resultList;
    }

    private HashMap<GPSSegment, List<GPSSegment>> findCandidateSegment(Collection<GPSSegment> roadTobeMatched, Collection<GPSSegment> roadNetwork) {
        // initialize tree
        TREE.addSegments(roadNetwork);

        HashMap<GPSSegment, List<GPSSegment>> candidateSegmentMap = new HashMap<GPSSegment, List<GPSSegment>>();
        for (GPSSegment segment : roadTobeMatched) {
            if (!candidateSegmentMap.containsKey(segment)) {
                candidateSegmentMap.put(segment, new ArrayList<GPSSegment>());
            }
            Collection<GPSSegment> searchSpace = TREE.getIntersectingSegment(segment);

            // we try to find the closest roadSegment for each point
            // in segment
            for (ILocation point : segment) {
                // initialize variables
                GPSSegment candidateSegment = null;
                double minDistance = Double.MAX_VALUE;

                for (GPSSegment roadSegment : searchSpace) {
                    double distance = getMinimumDistance(point, roadSegment);

                    if (distance < minDistance) {
                        // current roadPoint is the closest point
                        // set current roadSegment as candidateSegment
                        minDistance = distance;
                        candidateSegment = roadSegment;
                    }
                }

                if (candidateSegment != null) {
                    candidateSegmentMap.get(segment).add(candidateSegment);
                }
            }
        }
        return candidateSegmentMap;
    }

    // computes the minimal distance between point and segment
    private double getMinimumDistance(ILocation point, GPSSegment segment) {
        double minDistance = Double.MAX_VALUE;

        for (ILocation roadPoint : segment) {
            double distance = GPSCalc.getDistVincentyFast(
                    point.getLat(),
                    point.getLon(),
                    roadPoint.getLat(),
                    roadPoint.getLon());

            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private MapMatchSegment createMapMatchSegment(GPSSegment segment, GPSSegment candidateSegment) {
        MapMatchSegment mapMatchSegment = new MapMatchSegment();

        for (ILocation point : segment) {
            double minDistance = Double.MAX_VALUE;
            ILocation matchPoint = null;

            ILocation lastLocation = null;
            for (ILocation roadPoint : candidateSegment) {
                double distance = 0;
                if (lastLocation == null) {
                    distance = GPSCalc
                            .getDistVincentyFast(
                            point.getLat(), point.getLon(),
                            roadPoint.getLat(), roadPoint.getLon());
                } else {
                    // project point to edge lastpoint - roadPoint
                    ILocation projectionPoint = GPSCalc.getProjectionPoint(point, lastLocation, roadPoint);

                    if (projectionPoint != null) {
                        distance = GPSCalc.getDistVincentyFast(point.getLat(), point.getLon(), projectionPoint.getLat(), projectionPoint.getLon());
                        roadPoint = projectionPoint;
                    } else {
                        LOG.log(Level.SEVERE, "Point {0} has no projection to segment {1} - {2}", new Object[]{point, lastLocation, roadPoint});
                    }
                }

                if (distance < minDistance) {
                    minDistance = distance;
                    matchPoint = roadPoint;
                }
                lastLocation = roadPoint;
            }

            if (matchPoint != null) {
                MapMatchResult mapMatchResult = new MapMatchResult(point, matchPoint, minDistance);
                mapMatchSegment.getSegment().add(mapMatchResult);
            }
        }

        return mapMatchSegment;
    }
    private static final Logger LOG = Logger.getLogger(CurveToCurveMapMatcher.class.getName());
}
