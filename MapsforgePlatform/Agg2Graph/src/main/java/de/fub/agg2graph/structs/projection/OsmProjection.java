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
package de.fub.agg2graph.structs.projection;

import de.fub.agg2graph.structs.GPSPoint;
import java.awt.geom.Point2D;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;

public class OsmProjection {

    private static final TileFactory tf = new EmptyTileFactory();
    public static final int ZOOM_LEVEL = tf.getInfo().getMaximumZoomLevel();

    public static double[] gpsToCartesian(GPSPoint p) {
        GeoPosition gp = new GeoPosition(p.getLat(), p.getLon());
        Point2D p2 = tf.geoToPixel(gp, ZOOM_LEVEL);
        return new double[]{p2.getX(), p2.getY()};
    }

    public static GPSPoint cartesianToGps(double x, double y) {
        Point2D p2 = new Point2D.Double(x, y);
        GeoPosition gp = tf.pixelToGeo(p2, ZOOM_LEVEL);
        GPSPoint gps = new GPSPoint(gp.getLatitude(), gp.getLongitude());
        return gps;
    }

    public static double LatToY(double lat, int zoomLevel) {
        double e = Math.sin(lat * (Math.PI / 180.0));
        if (Math.abs(e) > 0.9999) {
            e = 0.9999;
        }
        double y = tf.getInfo().getMapCenterInPixelsAtZoom(zoomLevel).getY() + 0.5
                * Math.log((1 + e) / (1 - e)) * -1
                * (tf.getInfo().getLongitudeRadianWidthInPixels(zoomLevel));
        return y;
    }

    public static double LonToX(double lon, int zoomLevel) {
        double x = tf.getInfo().getMapCenterInPixelsAtZoom(zoomLevel).getX() + lon
                * tf.getInfo().getLongitudeDegreeWidthInPixels(zoomLevel);
        return x;
    }

    public static double YToLat(int y, int zoom) {
        double wy = y;
        double e1 = (wy - tf.getInfo().getMapCenterInPixelsAtZoom(zoom).getY())
                / (-1 * tf.getInfo().getLongitudeRadianWidthInPixels(zoom));
        double e2 = (2 * Math.atan(Math.exp(e1)) - Math.PI / 2) / (Math.PI / 180.0);
        double lat = e2;
        return lat;
    }

    public static double XToLon(int x, int zoom) {
        double wx = x;
        double lon = (wx - tf.getInfo().getMapCenterInPixelsAtZoom(zoom).getX())
                / tf.getInfo().getLongitudeDegreeWidthInPixels(zoom);
        return lon;
    }

}
