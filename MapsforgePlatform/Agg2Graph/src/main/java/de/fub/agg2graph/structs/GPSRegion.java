package de.fub.agg2graph.structs;

import java.awt.geom.Rectangle2D;

/**
 * Specify a "rectangular" region from start point to the end point.
 *
 * invariant: minLocation <= maxLocation.
 */
public class GPSRegion {

    public GPSPoint minLocation;
    public GPSPoint maxLocation;

    public GPSPoint getMinLocation() {
        return minLocation;
    }

    public GPSPoint getMaxLocation() {
        return maxLocation;
    }

    public GPSRegion(GPSPoint start, GPSPoint end) {
        this.minLocation = new GPSPoint(Math.min(start.getLat(), end.getLat()),
                Math.min(start.getLon(), end.getLon()));
        this.maxLocation = new GPSPoint(Math.max(start.getLat(), end.getLat()),
                Math.max(start.getLon(), end.getLon()));
    }

    public GPSRegion(double minLat, double minLon, double maxLat, double maxLon) {
        this(new GPSPoint(minLat, minLon), new GPSPoint(maxLat, maxLon));
    }

    public Rectangle2D getRectangle() {
        double startLat = minLocation.getLat();
        double startLon = minLocation.getLon();
        return new Rectangle2D.Double(startLon, startLat,
                maxLocation.getLon() - startLon, maxLocation.getLat() - startLat);
    }

    public GPSRegion union(GPSRegion region) {
        GPSPoint min = GPSPoint.min(minLocation, region.minLocation);
        GPSPoint max = GPSPoint.max(maxLocation, region.maxLocation);

        return new GPSRegion(min, max);
    }

    public double getMaxLatitude() {
        return maxLocation.getLat();
    }

    public double getMaxLongitude() {
        return maxLocation.getLon();
    }

    public double getMinLatitude() {
        return minLocation.getLat();
    }

    public double getMinLongitude() {
        return minLocation.getLon();
    }

    public boolean contains(GPSPoint location) {
        return minLocation.getLon() <= location.getLon()
                && location.getLon() <= maxLocation.getLon()
                && minLocation.getLat() <= location.getLat()
                && location.getLat() <= maxLocation.getLat();
    }

    public boolean isEmpty() {
        return minLocation.getLon() == 0. && maxLocation.getLon() == 0.
                && minLocation.getLat() == 0. && maxLocation.getLat() == 0.;
    }

    @Override
    public String toString() {
        return "<" + minLocation + ", " + maxLocation + ">";
    }
}
