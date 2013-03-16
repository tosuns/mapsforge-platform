/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.utils;

import de.fub.agg2graph.structs.GPSCalc;
import de.fub.gpxmodule.xml.gpx.Wpt;

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
}
