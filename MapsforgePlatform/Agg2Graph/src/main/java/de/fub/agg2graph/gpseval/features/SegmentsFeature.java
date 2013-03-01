package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

import java.util.HashSet;
import java.util.Set;

/**
 * The SegmentsFeature determines the number of segments (GPS-signal-losses) of
 * a GPS-track.
 */
public class SegmentsFeature extends Feature {

    private Set<Integer> mSegments = new HashSet<>();

    @Override
    public void reset() {
        mSegments.clear();
    }

    @Override
    public void addWaypoint(Waypoint entry) {
        mSegments.add(entry.getmSegment());
    }

    @Override
    public double getResult() {
        return mSegments.size();
    }
}
