package de.fub.agg2graph.gpseval.data.file;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.data.filter.WaypointFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is the base-class for all TrackFiles.
 *
 * <p>It handles the filtering of data using WaypointFilters.</p> <p>Subclasses
 * only need to override the rawIterator-method which returns an Iterator for
 * the unfiltered Waypoints.</p>
 */
public abstract class TrackFile implements Iterable<Waypoint> {

    private Path mDataFile;
    private ArrayList<WaypointFilter> mFilters = new ArrayList<>();

    /**
     * Get the Iterator for the unfiltered Waypoints.
     *
     * To get only filtered Waypoints you shuld use iterator() instead.
     *
     * @return
     */
    protected abstract Iterator<Waypoint> rawIterator();

    /**
     * Add a waypoint filter that is used by the Iterator returned by
     * iterator().
     *
     * @param filter
     */
    public void addWaypointFilter(WaypointFilter filter) {
        mFilters.add(filter);
    }

    /**
     * Set the path for the GPS-data-file.
     *
     * @param dataFile
     */
    public void setDataFile(Path dataFile) {
        mDataFile = dataFile;
    }

    /**
     * Get the path of the GPS-data-file.
     *
     * @return
     */
    public Path getDataFile() {
        return mDataFile;
    }

    @Override
    public Iterator<Waypoint> iterator() {
        return new FilteredGPSDataIterator();
    }

    /**
     * This Itreator returns only those Waypoints which passes all waypoint
     * filters.
     */
    private class FilteredGPSDataIterator implements Iterator<Waypoint> {

        private Iterator<Waypoint> mRawIterator;
        private Waypoint nextGpsData = null;

        public FilteredGPSDataIterator() {
            mRawIterator = rawIterator();
        }

        @Override
        public boolean hasNext() {
            nextGpsData = null;

            while (mRawIterator.hasNext()) {
                Waypoint gpsData = mRawIterator.next();

                // check if the waypoint passes each waypoint filter
                boolean passedAllFilter = true;
                for (WaypointFilter filter : mFilters) {
                    if (!filter.filter(gpsData)) {
                        passedAllFilter = false;
                        break;
                    }
                }
                if (passedAllFilter) {
                    nextGpsData = gpsData;
                    return true;
                }
            }

            return false;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Waypoint next() {
            return nextGpsData;
        }
    }
}
