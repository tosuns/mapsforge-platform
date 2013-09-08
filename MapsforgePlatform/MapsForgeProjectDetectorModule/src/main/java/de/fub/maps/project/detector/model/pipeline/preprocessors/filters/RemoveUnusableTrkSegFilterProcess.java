/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Properties;
import de.fub.maps.project.detector.model.xmls.Property;
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
    + "removed from the track.",
    "CLT_RemoveUnusable_Trkseg_Filter_Property_RemAllSegShorterThan_Name=Remove short Segment on/off",
    "CLT_RemoveUnusable_Trkseg_Filter_Property_RemAllSegShorterThan_Description=(De) activates the option whether segments with a length shorter than specified be the property Segment length should be removed or not",
    "CLT_RemoveUnusable_Trkseg_Filter_Property_RemoveSegementLength_Name=Segment length",
    "CLT_RemoveUnusable_Trkseg_Filter_Property_RemoveSegementLength_Description=Segments which are shorter then the specified length will be removed if the the property Remove short segment on/off is activeated."
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

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(RemoveUnusableTrkSegFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_RemoveUnusable_Trkseg_Filter_Name());
        descriptor.setDescription(Bundle.CLT_RemoveUnusable_Trkseg_Filter_description());

        Property property = new Property();
        property.setId(PROP_NAME_REMOVE_SHORT_SEGMENT_ACTIVE);
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.FALSE.toString());
        property.setName(Bundle.CLT_RemoveUnusable_Trkseg_Filter_Property_RemAllSegShorterThan_Name());
        property.setDescription(Bundle.CLT_RemoveUnusable_Trkseg_Filter_Property_RemAllSegShorterThan_Description());
        descriptor.getProperties().getPropertyList().add(property);

        property = new Property();
        property.setId(PROP_NAME_SEGMENT_LENGTH);
        property.setJavaType(Double.class.getName());
        property.setValue("50");
        property.setName(Bundle.CLT_RemoveUnusable_Trkseg_Filter_Property_RemoveSegementLength_Name());
        property.setDescription(Bundle.CLT_RemoveUnusable_Trkseg_Filter_Property_RemoveSegementLength_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
