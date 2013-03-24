/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_RemoveUnusable_Trkseg_Filter_Name=Remove Unusable Tracks",
    "CLT_RemoveUnusable_Trkseg_Filter_description=This filter remove tracks, "
    + "whose content don't have sufficient data. A Segment that does not have "
    + "at least the time stamp. latitude and longitude information will be "
    + "removed from the track."
})
@ServiceProvider(service = FilterProcess.class)
public class RemoveUnusableTrkSegFilterProcess extends FilterProcess {

    private static final Logger LOG = Logger.getLogger(RemoveUnusableTrkSegFilterProcess.class.getName());
    private List<TrackSegment> gpxTracks;

    public RemoveUnusableTrkSegFilterProcess() {
    }

    public RemoveUnusableTrkSegFilterProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        ArrayList<TrackSegment> arrayList = new ArrayList<TrackSegment>(gpxTracks);
        for (TrackSegment trackSegment : arrayList) {
            for (Waypoint gpxWpt : trackSegment.getWayPointList()) {
                if (gpxWpt.getTimestamp() == null || gpxWpt.getTimestamp().getTime() == 0) {
                    gpxTracks.remove(trackSegment);
                    LOG.log(Level.FINE, MessageFormat.format("Segment {0} removed from track {1}", trackSegment, gpxTracks));
                    break;
                }
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_RemoveUnusable_Trkseg_Filter_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_RemoveUnusable_Trkseg_Filter_description();
    }

    @Override
    public void setInput(List<TrackSegment> input) {
        this.gpxTracks = input;
    }

    @Override
    public List<TrackSegment> getResult() {
        return this.gpxTracks;
    }
}
