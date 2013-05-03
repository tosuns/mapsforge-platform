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
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = FilterProcess.class)
public class UniformLengthSegmentationFilterProcess extends FilterProcess {

    private static final String PROP_NAME_LENGTH = "uniform.length.filter.length";
    private List<TrackSegment> gpsTracks;
    private List<TrackSegment> result = new LinkedList<TrackSegment>();
    // length  in meters
    private double length = -1;

    public UniformLengthSegmentationFilterProcess() {
    }

    public UniformLengthSegmentationFilterProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        if (gpsTracks != null) {
            if (result== null) {
                result = new ArrayList<TrackSegment>(gpsTracks.size());
            }
            result.clear();
            TrackSegment shortSegement = new TrackSegment();

            for (TrackSegment trackSegment : gpsTracks) {
                // reset variables
                double currentLength = 0;
                Waypoint lastWaypoint = null;

                for (Waypoint waypoint : trackSegment.getWayPointList()) {
                    if (lastWaypoint != null) {
                        // only if there is a reference point fot the computation of the
                        // length we will compute.
                        currentLength += GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), waypoint.getLon(), waypoint.getLat(), waypoint.getLon());

                        if (currentLength > getLengthProperty()) {
                            // current length is longer then the specified
                            // reference value. we add the current short segemtn
                            // to the list and reset variables
                            result.add(shortSegement);
                            currentLength = 0;
                            shortSegement = new TrackSegment();
                        } else {
                            // we are still in the specified length constrain
                            // and add the waypoint to the short segement
                            shortSegement.add(waypoint);
                        }
                    }
                    lastWaypoint = waypoint;
                }

                // check whether the current shortsegement is already in the result list
                // if not add it to the list.
                if (!result.contains(shortSegement)) {
                    result.add(shortSegement);
                }
            }
        }
    }

    @Messages("CLT_UniformLengthFilter_Name=Uniform Length Segmentation")
    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_UniformLengthFilter_Name();
    }

    @Messages("CLT_UniformLengthFilter_Description=GPS traces will be segmented into equally long tracks.")
    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_UniformLengthFilter_Description();
    }

    @Override
    public void setInput(List<TrackSegment> input) {
        this.gpsTracks = input;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> arrayList = this.result;
        this.result = null;
        this.gpsTracks = null;
        return arrayList;
    }

    private double getLengthProperty() {
        if (length < 0) {
            if (getProcessDescriptor() != null) {
                List<Property> propertyList = getProcessDescriptor().getProperties().getPropertyList();
                for (Property property : propertyList) {
                    if (PROP_NAME_LENGTH.equals(property.getId())) {
                        if (property.getValue() != null) {
                            length = Double.valueOf(property.getValue());
                        } else {
                            length = 30; // 5 meter default setting
                        }
                        break;
                    }
                }
            }
        }
        return length;
    }
}
