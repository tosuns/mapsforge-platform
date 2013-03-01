package de.fub.agg2graph.gpseval.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a single waypoint of a GPS-track.
 */
public class Waypoint {

    private static final Logger LOG = Logger.getLogger(Waypoint.class.getName());
    protected double mSpeed;
    protected double mBearing;
    protected int mSegment;
    protected int mPrecision;
    protected Date mDate;
    protected double mLat;
    protected double mLon;

    public Waypoint() {
    }

    // TODO: better constructor for non-csv-files
    public Waypoint(String[] data) {
        mSpeed = Double.parseDouble(data[7].replaceFirst(",", "."));
        mSegment = Integer.parseInt(data[0]);
        mPrecision = Integer.parseInt(data[6]);
        mBearing = Double.parseDouble(data[5].replaceFirst(",", "."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
        try {
            mDate = sdf.parse(data[8].replaceFirst("T", " "));
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error parsing Date: {0}", ex.getMessage());
        }
        mLat = Double.parseDouble(data[2]);
        mLon = Double.parseDouble(data[3]);

    }

    public double getmSpeed() {
        return mSpeed;
    }

    public double getmBearing() {
        return mBearing;
    }

    public int getmSegment() {
        return mSegment;
    }

    public int getmPrecision() {
        return mPrecision;
    }

    public Date getmDate() {
        return mDate;
    }

    public double getmLat() {
        return mLat;
    }

    public double getmLon() {
        return mLon;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.mSpeed) ^ (Double.doubleToLongBits(this.mSpeed) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.mBearing) ^ (Double.doubleToLongBits(this.mBearing) >>> 32));
        hash = 41 * hash + this.mSegment;
        hash = 41 * hash + this.mPrecision;
        hash = 41 * hash + Objects.hashCode(this.mDate);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.mLat) ^ (Double.doubleToLongBits(this.mLat) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.mLon) ^ (Double.doubleToLongBits(this.mLon) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Waypoint other = (Waypoint) obj;
        if (Double.doubleToLongBits(this.mSpeed) != Double.doubleToLongBits(other.mSpeed)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mBearing) != Double.doubleToLongBits(other.mBearing)) {
            return false;
        }
        if (this.mSegment != other.mSegment) {
            return false;
        }
        if (this.mPrecision != other.mPrecision) {
            return false;
        }
        if (!Objects.equals(this.mDate, other.mDate)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mLat) != Double.doubleToLongBits(other.mLat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mLon) != Double.doubleToLongBits(other.mLon)) {
            return false;
        }
        return true;
    }
}
