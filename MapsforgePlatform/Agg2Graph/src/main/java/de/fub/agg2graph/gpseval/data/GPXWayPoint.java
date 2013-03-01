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
public class GPXWayPoint extends Waypoint {

    public GPXWayPoint() {
        super();
    }

    public void setmSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public void setmBearing(double mBearing) {
        this.mBearing = mBearing;
    }

    public void setmSegment(int mSegment) {
        this.mSegment = mSegment;
    }

    public void setmPrecision(int mPrecision) {
        this.mPrecision = mPrecision;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public void setmLon(double mLon) {
        this.mLon = mLon;
    }
}
