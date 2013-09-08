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
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.inference.InferenceMode;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@Messages({"CLT_UniformDurationFilter_Name=Uniform Duration Segmentation",
    "CLT_UniformDurationFilter_Description=GPS tracks will be segmented into tracks whose duration time does not exceeds the specified duration (seconds) limit property.",
    "CLT_UniformDurationFilter_Property_MaxDuration_Name=Maximum Duration",
    "CLT_UniformDurationFilter_Property_MaxDuration_Description=Specifies the maximum duration for each gps track."
})
@ServiceProvider(service = FilterProcess.class)
public class UniformDurationSegmentationFilterProcess extends FilterProcess {

    private static final String PROP_NAME_DURATION = "uniform.duration.filter.duration";
    private static final Logger LOG = Logger.getLogger(UniformDurationSegmentationFilterProcess.class.getName());
    private List<TrackSegment> result;
    private List<TrackSegment> gpsTracks;
    // duration in seconds;
    private double duration = -1;

    public UniformDurationSegmentationFilterProcess() {
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
                        if (PROP_NAME_DURATION.equals(property.getId())) {
                            duration = Double.valueOf(property.getValue());
                        } else if (PROP_NAME_FILTER_SCOPE.equals(property.getId())) {
                            scope = InferenceMode.fromValue(property.getValue());
                        }
                    } catch (NumberFormatException ex) {
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

                // keep only segment with a duration close to getDurationProperty
//                if (!result.contains(currentSegment)) {
//                    result.add(currentSegment);
//                }
            }

        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_UniformDurationFilter_Name();
    }

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
        List<TrackSegment> arrayList = this.result;
        this.result = null;
        this.gpsTracks = null;
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

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(UniformDurationSegmentationFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_UniformDurationFilter_Name());
        descriptor.setDescription(Bundle.CLT_UniformDurationFilter_Description());

        Property property = new Property();
        property.setId(PROP_NAME_FILTER_SCOPE);
        property.setJavaType(InferenceMode.class.getName());
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_Scope_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_Scope_Description());
        property.setValue(InferenceMode.INFERENCE_MODE.toString());
        descriptor.getProperties().getPropertyList().add(property);

        // <!-- duration value in seconds -->
        property = new Property();
        property.setId(PROP_NAME_DURATION);
        property.setJavaType(Double.class.getName());
        property.setValue("300");
        property.setName(Bundle.CLT_UniformDurationFilter_Property_MaxDuration_Name());
        property.setDescription(Bundle.CLT_UniformDurationFilter_Property_MaxDuration_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
