/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = FilterProcess.class)
public class UniformDurationSegmentationFilterProcess extends FilterProcess {

    private static final String PROP_NAME_DURATION = "uniform.duration.filter.duration";
    private List<TrackSegment> result;
    private List<TrackSegment> gpsTracks;
    // duration in seconds;
    private double duration = -1;

    public UniformDurationSegmentationFilterProcess() {
    }

    public UniformDurationSegmentationFilterProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        if (gpsTracks != null) {
            result.clear();

            for (TrackSegment trackSegment : gpsTracks) {
                Waypoint lastWaypoint = null;
                long currentDuration = 0;
                TrackSegment currentSegment = new TrackSegment();

                for (Waypoint waypoint : trackSegment.getWayPointList()) {
                    if (lastWaypoint != null) {
                        // get time difference in seconds and add to current duration
                        currentDuration += (waypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000;

                        // if currenDuration exceeds the duration limit
                        // then add current segment to result and reset
                        // variables, otherwise add waypoint to current segment
                        if (currentDuration > getDurationProperty()) {
                            result.add(currentSegment);
                            currentDuration = 0;
                            currentSegment = new TrackSegment();
                        } else {
                            currentSegment.add(waypoint);
                        }
                    }
                    lastWaypoint = waypoint;
                }

                if (!result.contains(currentSegment)) {
                    result.add(currentSegment);
                }
            }

        }
    }

    @Messages("CLT_UniformDurationFilter_Name=Uniform Duration Segmentation")
    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_UniformDurationFilter_Name();
    }

    @Messages("CLT_UniformDurationFilter_Description=GPS tracks will be segmented into tracks whose duration time does not exceeds the specified duration (seconds) limit property.")
    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_UniformDurationFilter_Description();
    }

    @Override
    public void setInput(List<TrackSegment> input) {
        this.gpsTracks = input;
    }

    @Override
    public List<TrackSegment> getResult() {
        ArrayList<TrackSegment> arrayList = new ArrayList<TrackSegment>(this.result);
        this.result.clear();
        return arrayList;

    }

    private double getDurationProperty() {
        if (duration < 0) {
            List<Property> propertyList = getProcessDescriptor().getProperties().getPropertyList();
            for (Property property : propertyList) {
                if (PROP_NAME_DURATION.equals(property.getId())) {
                    if (property.getValue() != null) {
                        duration = Double.valueOf(property.getValue());
                    } else {
                        // duration in seconds (5 min default)
                        duration = 300;
                    }
                    break;
                }
            }
        }
        return duration;
    }
}
