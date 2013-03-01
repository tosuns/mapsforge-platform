/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.data.file;

import de.fub.agg2graph.gpseval.data.GPXWayPoint;
import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import java.util.Iterator;

/**
 * One GPXTrackFile represents one GPSSegement of a GPX Track.
 *
 * @author Serdar
 */
public class GPXTrackFile extends TrackFile {

    private final GPSSegment gpsSegment;

    public GPXTrackFile(GPSSegment segment) {
        this.gpsSegment = segment;
    }

    @Override
    protected Iterator<Waypoint> rawIterator() {
        return new GPXWayPointIterator(this.gpsSegment);
    }

    private static class GPXWayPointIterator implements Iterator<Waypoint> {

        private Waypoint currentGpxWayPoint = null;
        private final GPSSegment gpxSegment;
        private int index = 0;

        public GPXWayPointIterator(GPSSegment segment) {
            assert segment != null;
            this.gpxSegment = segment;
        }

        @Override
        public boolean hasNext() {

            if (index > -1 && index < gpxSegment.size()) {
                GPSPoint gpsPoint = gpxSegment.get(index);
                GPXWayPoint gpxWayPoint = new GPXWayPoint();
                gpxWayPoint.setmLat(gpsPoint.getLat());
                gpxWayPoint.setmLon(gpsPoint.getLon());
                gpxWayPoint.setmDate(gpsPoint.getTimestamp());

                if (gpsPoint.getTimestamp() != null && index > 0) {
                    GPSPoint prevPoint = gpxSegment.get(index - 1);

                    if (prevPoint.getTimestamp() != null) {
                        double distance = GPSCalc.getDistVincentyFast(gpsPoint.getLat(), gpsPoint.getLon(), prevPoint.getLat(), prevPoint.getLon());
                        long timeDiffInSecs = (gpsPoint.getTimestamp().getTime() - prevPoint.getTimestamp().getTime()) / 1000;
                        gpxWayPoint.setmSpeed(distance / timeDiffInSecs);
                    }
                }
                index++;
            }

            return false;
        }

        @Override
        public Waypoint next() {
            return currentGpxWayPoint;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
