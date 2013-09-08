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

import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.inference.features.TrackLengthFeatureProcess;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MinimumTrackLengthFilter_Name=Minimum Track Length Filter",
    "CLT_MinimumTrackLengthFilter_Description=This process filters all track segments whose length is shorter then the specified min track length parameter.",
    "CLT_MinimumTrackLengthFilter_Property_TrackLength_Name=Minimum track length",
    "CLT_MinimumTrackLengthFilter_Property_TrackLength_Description=Specifies the minimum length (in meters) that a track must have."
})
@ServiceProvider(service = FilterProcess.class)
public class MinimumTrackLengthFilterProcess extends FilterProcess {

    private static final String PROP_NAME_MIN_TRACK_LENGTH = "min.track.length";
    private Double minTrackLength = null;
    private List<TrackSegment> trackSegments;
    private final TrackLengthFeatureProcess trackLengthfeature = new TrackLengthFeatureProcess();

    public MinimumTrackLengthFilterProcess() {
    }

    @Override
    protected void start() {
        for (TrackSegment trackSegment : new ArrayList<TrackSegment>(trackSegments)) {
            trackLengthfeature.setInput(trackSegment);
            trackLengthfeature.run();
            Double result = trackLengthfeature.getResult();
            if (result <= getMinTrackLengthParameter()) {
                trackSegments.remove(trackSegment);
            }
        }
        trackLengthfeature.setInput(null);

    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_MinimumTrackLengthFilter_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_MinimumTrackLengthFilter_Description();
    }

    @Override
    public void setInput(List<TrackSegment> input) {
        this.trackSegments = input;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> list = trackSegments;
        trackSegments = null;
        return list;
    }

    private double getMinTrackLengthParameter() {
        if (minTrackLength == null) {
            List<Property> propertyList = getProcessDescriptor().getProperties().getPropertyList();
            for (Property property : propertyList) {
                if (PROP_NAME_MIN_TRACK_LENGTH.equals(property.getId()) && property.getValue() != null) {
                    try {
                        minTrackLength = Double.valueOf(property.getValue());
                    } catch (NumberFormatException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    break;
                }
            }
            if (minTrackLength == null) {
                minTrackLength = 150d;
            }
        }
        return minTrackLength;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MinimumTrackLengthFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_MinimumTrackLengthFilter_Name());
        descriptor.setDescription(Bundle.CLT_MinimumTrackLengthFilter_Description());

        Property property = new Property();
        property.setId(PROP_NAME_PROCESS_STATE);
        property.setJavaType(Double.class.getName());
        property.setValue("150");
        property.setName(Bundle.CLT_MinimumTrackLengthFilter_Property_TrackLength_Name());
        property.setDescription(Bundle.CLT_MinimumTrackLengthFilter_Property_TrackLength_Description());

        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
