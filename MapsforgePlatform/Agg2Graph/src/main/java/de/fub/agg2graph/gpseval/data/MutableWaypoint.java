/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.data;

import java.util.Date;

/**
 *
 * @author Serdar
 */
public class MutableWaypoint extends Waypoint {

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public void setSegment(int segment) {
        this.segment = segment;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
