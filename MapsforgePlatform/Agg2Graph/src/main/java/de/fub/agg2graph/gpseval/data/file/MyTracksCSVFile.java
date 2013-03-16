package de.fub.agg2graph.gpseval.data.file;

import au.com.bytecode.opencsv.CSVReader;
import de.fub.agg2graph.gpseval.data.Waypoint;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MyTracksCSVFile reads GPS-data from a CSV-file exported by the Android-app
 * MyTracks.
 *
 * <p>
 * The first four lines of the file are skipped because they contain additional
 * information.
 * </p>
 *
 * <p>
 * The following lines contain comma-separated values enclosed by quotes. They
 * have the format: segments, number, latitude, longitude, height, bearing,
 * precision, speed, time, ununsed, ununsed, ununsed, ununsed.
 * </p>
 */
public class MyTracksCSVFile extends TrackFile {

    @Override
    protected Iterator<Waypoint> rawIterator() {
        return new GPSDataIterator();
    }

    /**
     * Read CSV-file and return {@link de.fub.agg2graph.gpseval.data.Waypoint
     * Waypoint}-objects for each data-line.
     */
    private class GPSDataIterator implements Iterator<Waypoint> {

        private CSVReader mReader;
        private Waypoint mNextGpsData = null;

        public GPSDataIterator() {
            try {
                mReader = new CSVReader(
                        new FileReader(getDataFile().toString()), ',', '"', 4);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MyTracksCSVFile.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

        @Override
        public boolean hasNext() {
            mNextGpsData = null;

            try {
                String[] data = mReader.readNext();
                if (data != null) {
                    mNextGpsData = new Waypoint(data);
                    return true;
                }

                return false;

            } catch (IOException ex) {
                Logger.getLogger(MyTracksCSVFile.class.getName()).log(
                        Level.SEVERE, null, ex);
                return false;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Waypoint next() {
            return mNextGpsData;
        }
    }
}
