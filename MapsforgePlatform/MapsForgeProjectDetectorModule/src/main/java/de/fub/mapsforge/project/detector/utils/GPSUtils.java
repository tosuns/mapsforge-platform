/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.utils;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.gpxmodule.xml.Gpx;
import de.fub.gpxmodule.xml.Trk;
import de.fub.gpxmodule.xml.Trkseg;
import de.fub.gpxmodule.xml.Wpt;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Serdar
 */
public class GPSUtils {

    public static double computeVelocity(Wpt previousWpt, Wpt currentWpt) {

        if (previousWpt != null && currentWpt != null
                && previousWpt.getTime() != null && currentWpt.getTime() != null) {
            double distanceInMeters = GPSCalc.getDistVincentyFast(
                    previousWpt.getLat().doubleValue(),
                    previousWpt.getLon().doubleValue(),
                    currentWpt.getLat().doubleValue(),
                    currentWpt.getLon().doubleValue());

            long timeInSec = Math.max(0, (currentWpt.getTime().getTime() - previousWpt.getTime().getTime() / 1000));
            double velocity = 0;
            if (timeInSec > 0) {
                velocity = distanceInMeters / (timeInSec);
            }
            return velocity;
        }
        return 0;
    }

    public static Gpx convert(Collection<TrackSegment> trackSegments) {
        Gpx gpx = new Gpx();
        gpx.setCreator("Mapsforge Detector");
        Trk trk = new Trk();

        for (TrackSegment trackSegment : trackSegments) {
            Trkseg trkseg = convert(trackSegment);
            if (!trkseg.getTrkpt().isEmpty()) {
                trk.getTrkseg().add(trkseg);
            }
        }

        gpx.getTrk().add(trk);
        return gpx;
    }

    public static Trkseg convert(TrackSegment trackSegment) {
        Trkseg trkseg = new Trkseg();

        for (Waypoint waypoint : trackSegment.getWayPointList()) {
            Wpt gpxWpt = new Wpt();
            double lat = waypoint.getLat();
            double lon = waypoint.getLon();
            Date timestamp = waypoint.getTimestamp();
            gpxWpt.setLat(BigDecimal.valueOf(lat));
            gpxWpt.setLon(BigDecimal.valueOf(lon));
            gpxWpt.setTime(timestamp);
            trkseg.getTrkpt().add(gpxWpt);
        }

        return trkseg;
    }
}
