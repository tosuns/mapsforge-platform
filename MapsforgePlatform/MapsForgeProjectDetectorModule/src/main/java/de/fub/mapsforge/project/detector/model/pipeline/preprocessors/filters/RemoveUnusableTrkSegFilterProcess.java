/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.Properties;
import de.fub.mapsforge.project.detector.model.xmls.Property;
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
    private static final String PROP_NAME_REMOVE_SHORT_SEGMENT_ACTIVE = "remove.all.segments.shorter.than";
    private static final String PROP_NAME_SEGMENT_LENGTH = "to.be.removed.segments";
    private List<TrackSegment> gpxTracks;
    private Double segmentLength;
    private Boolean removeShortSegments;

    public RemoveUnusableTrkSegFilterProcess() {
    }

    public RemoveUnusableTrkSegFilterProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        ArrayList<TrackSegment> arrayList = new ArrayList<TrackSegment>(gpxTracks);
        OUTERLOOP: // Marker
        for (TrackSegment trackSegment : arrayList) {
            double length = 0;
            Waypoint lastWaypoint = null;
            // check
            for (Waypoint gpxWpt : trackSegment.getWayPointList()) {

                // check whether there is a time stamp in the data
                if (gpxWpt.getTimestamp() == null || gpxWpt.getTimestamp().getTime() == 0) {
                    gpxTracks.remove(trackSegment);
                    continue OUTERLOOP;
                } else if (!isPropertyRemoveShortSegmentActive()) {
                    // if the shortlength property is not active skip the rest
                    // of the loop
                    continue OUTERLOOP;
                }

                if (lastWaypoint != null) {
                    length += GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), gpxWpt.getLat(), gpxWpt.getLon());
                }

                lastWaypoint = gpxWpt;
            }
            // remove short segments
            if (isPropertyRemoveShortSegmentActive() && length <= getPropertySegmentLength()) {
                gpxTracks.remove(trackSegment);
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
        List<TrackSegment> trackSegments = this.gpxTracks;
        this.gpxTracks = null;
        return trackSegments;
    }

    private Boolean isPropertyRemoveShortSegmentActive() {
        if (removeShortSegments == null) {
            removeShortSegments = false;
            List<Property> propertyList = getProcessDescriptor().getProperties().getPropertyList();
            for (Property property : propertyList) {
                if (property.getValue() != null
                        && property.getId() != null
                        && PROP_NAME_REMOVE_SHORT_SEGMENT_ACTIVE.equals(property.getId())) {
                    try {
                        removeShortSegments = Boolean.valueOf(property.getValue());
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                    break;
                }
            }
        }
        return removeShortSegments;
    }

    private Double getPropertySegmentLength() {
        if (segmentLength == null) {
            segmentLength = 50d;
            Properties properties = getProcessDescriptor().getProperties();
            for (Property property : properties.getPropertyList()) {
                if (property.getId() != null
                        && PROP_NAME_SEGMENT_LENGTH.equals(property.getId())
                        && property.getValue() != null) {
                    try {
                        segmentLength = Double.valueOf(property.getValue());
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                    break;
                }
            }
        }
        return segmentLength;
    }
}
