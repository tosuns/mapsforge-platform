/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = MapMatcher.class)
public class PointToPointMapMatcher implements MapMatcher {

    @Override
    public List<MapMatchSegment> findMatch(Collection<GPSSegment> tobeMatchedRoadNetwork, Collection<GPSSegment> roadNetwork) {
        List<MapMatchSegment> resultList = new ArrayList<MapMatchSegment>(roadNetwork.size());

        for (GPSSegment roadSegment : tobeMatchedRoadNetwork) {
            MapMatchSegment matchSegment = new MapMatchSegment();

            for (GPSPoint point : roadSegment) {

                // do point to point matching
                MapMatchResult mapMatchResult = findMatchingPoint(point, roadNetwork);

                if (mapMatchResult != null) {
                    matchSegment.getSegment().add(mapMatchResult);
                }
            }

            if (!matchSegment.getSegment().isEmpty()) {
                resultList.add(matchSegment);
            }
        }
        return resultList;
    }

    private MapMatchResult findMatchingPoint(GPSPoint point, Collection<GPSSegment> roadNetwork) {
        MapMatchResult mapMatchResult = null;
        double minDistance = Double.MAX_VALUE;
        GPSPoint matchPoint = null;
        for (GPSSegment matchGpsSegment : roadNetwork) {
            for (GPSPoint roadPoint : matchGpsSegment) {

                double distance = GPSCalc.getDistVincentyFast(
                        point.getLat(),
                        point.getLon(),
                        roadPoint.getLat(),
                        roadPoint.getLon());

                if (distance < minDistance) {
                    minDistance = distance;
                    matchPoint = roadPoint;
                }
            }
        }

        if (matchPoint != null) {
            mapMatchResult = new MapMatchResult(point, matchPoint, minDistance);
        }

        return mapMatchResult;
    }
}
