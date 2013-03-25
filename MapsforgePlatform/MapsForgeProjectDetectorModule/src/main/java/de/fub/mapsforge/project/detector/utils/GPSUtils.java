/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.utils;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.gpxmodule.xml.gpx.Trk;
import de.fub.gpxmodule.xml.gpx.Trkseg;
import de.fub.gpxmodule.xml.gpx.Wpt;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Serdar
 */
public class GPSUtils {

    public static double computeSpeed(Wpt previousWpt, Wpt currentWpt) {

        if (previousWpt != null && currentWpt != null) {
            double distanceInMeters = GPSCalc.getDistVincentyFast(
                    previousWpt.getLat().doubleValue(),
                    previousWpt.getLon().doubleValue(),
                    currentWpt.getLat().doubleValue(),
                    currentWpt.getLon().doubleValue());

            long timeInMilliSec = Math.abs(currentWpt.getTime().getTime() - previousWpt.getTime().getTime());

            double speed = distanceInMeters / (timeInMilliSec / 1000);

            return speed;
        }
        return 0;
    }

    public static Gpx convert(List<TrackSegment> trackSegments) {
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
