/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@Messages({
    "CLT_UniformLengthFilter_Name=Uniform Length Segmentation",
    "CLT_UniformLengthFilter_Description=GPS traces will be segmented into equally long tracks.",
    "CLT_UniformLengthFilter_Property_Length_Name=Maximum Length",
    "CLT_UniformLengthFilter_Property_Length_Description=Specifies the maximum length for each gps track."
})
@ServiceProvider(service = FilterProcess.class)
public class UniformLengthSegmentationFilterProcess extends FilterProcess {

    private static final String PROP_NAME_LENGTH = "uniform.length.filter.length";
    private static final Logger LOG = Logger.getLogger(UniformLengthSegmentationFilterProcess.class.getName());
    private List<TrackSegment> gpsTracks;
    private List<TrackSegment> result = new LinkedList<TrackSegment>();
    // length  in meters
    private double length = -1;

    public UniformLengthSegmentationFilterProcess() {
    }

    @Override
    protected void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        super.setProcessDescriptor(processDescriptor);
        init();
    }

    private void init() {
        ProcessDescriptor descriptor = getProcessDescriptor();
        if (descriptor != null) {
            List<Property> propertyList = descriptor.getProperties().getPropertyList();
            for (Property property : propertyList) {
                if (property.getValue() != null) {
                    try {
                        if (PROP_NAME_LENGTH.equals(property.getValue())) {
                            length = Double.valueOf(property.getValue());
                        } else if (PROP_NAME_FILTER_SCOPE.equals(property.getValue())) {
                            scope = InferenceMode.valueOf(property.getValue());
                        }
                    } catch (IllegalArgumentException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    @Override
    protected void start() {
        if (gpsTracks != null) {
            if (result == null) {
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
//                if (!result.contains(shortSegement)) {
//                    result.add(shortSegement);
//                }
            }
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_UniformLengthFilter_Name();
    }

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

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(UniformLengthSegmentationFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_UniformLengthFilter_Name());
        descriptor.setDescription(Bundle.CLT_UniformLengthFilter_Description());

        Property property = new Property();
        property.setId(PROP_NAME_FILTER_SCOPE);
        property.setJavaType(InferenceMode.class.getName());
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_Scope_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_Scope_Description());
        property.setValue(InferenceMode.INFERENCE_MODE.toString());
        descriptor.getProperties().getPropertyList().add(property);

        // <!-- length value in meters -->
        property = new Property();
        property.setId(PROP_NAME_LENGTH);
        property.setJavaType(Double.class.getName());
        property.setValue("10");
        property.setName(Bundle.CLT_UniformLengthFilter_Property_Length_Name());
        property.setDescription(Bundle.CLT_UniformLengthFilter_Property_Length_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
