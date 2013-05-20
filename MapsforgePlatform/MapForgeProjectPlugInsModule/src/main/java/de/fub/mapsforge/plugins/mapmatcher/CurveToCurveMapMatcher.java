/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.mapmatcher;

import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = MapMatcher.class)
public class CurveToCurveMapMatcher implements MapMatcher {

    @Override
    public List<MapMatchSegment> findMatch(Collection<GPSSegment> roadTobeMatched, Collection<GPSSegment> roadNetwork) {
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
        HashMap<GPSSegment, List<GPSSegment>> candidateSegmentMap = new HashMap<GPSSegment, List<GPSSegment>>();
        for (GPSSegment segment : roadTobeMatched) {
            if (!candidateSegmentMap.containsKey(segment)) {
                candidateSegmentMap.put(segment, new ArrayList<GPSSegment>());
            }

            // we try to find the closest roadSegment for each point
            // in segment
            for (ILocation point : segment) {
                // initialize variables
                GPSSegment candidateSegment = null;
                double minDistance = Double.MAX_VALUE;

                for (GPSSegment roadSegment : roadNetwork) {
                    for (ILocation roadPoint : roadSegment) {
                        double distance = GPSCalc.getDistVincentyFast(
                                point.getLat(),
                                point.getLon(),
                                roadPoint.getLat(),
                                roadPoint.getLon());

                        if (distance < minDistance) {
                            // current roadPoint is the closest point
                            // set current roadSegment as candidateSegment

                            minDistance = distance;
                            candidateSegment = roadSegment;
                        }
                    }
                }

                if (candidateSegment != null) {
                    candidateSegmentMap.get(segment).add(candidateSegment);
                }
            }
        }
        return candidateSegmentMap;
    }

    private MapMatchSegment createMapMatchSegment(GPSSegment segment, GPSSegment candidateSegment) {
        MapMatchSegment mapMatchSegment = new MapMatchSegment();

        for (ILocation point : segment) {
            double minDistance = Double.MAX_VALUE;
            ILocation matchPoint = null;

            for (ILocation roadPoint : candidateSegment) {
                double distance = GPSCalc.getDistVincentyFast(
                        point.getLat(), point.getLon(),
                        roadPoint.getLat(), roadPoint.getLon());

                if (distance < minDistance) {
                    matchPoint = roadPoint;
                }

            }

            if (matchPoint != null) {
                MapMatchResult mapMatchResult = new MapMatchResult(point, matchPoint, minDistance);
                mapMatchSegment.getSegment().add(mapMatchResult);
            }
        }

        return mapMatchSegment;
    }
}
