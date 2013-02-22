package de.fub.agg2graph.gpseval.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents a single waypoint of a GPS-track.
 */
public class Waypoint {

	public double mSpeed;
	public double mBearing;
	public int mSegment;
	public int mPrecision;
	public Date mDate;
	public double mLat;
	public double mLon;

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
			System.out.println("Error parsing Date: " + ex.getMessage());
		}
		mLat = Double.parseDouble(data[2]);
		mLon = Double.parseDouble(data[3]);

	}

	public boolean equals(Waypoint wp) {
		return mSpeed == wp.mSpeed && mBearing == wp.mBearing
				&& mSegment == wp.mSegment && mPrecision == wp.mPrecision
				&& mDate.equals(wp.mDate) && mLat == wp.mLat && mLon == wp.mLon;
	}
}
