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
package de.fub.maps.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.StopRateFeature;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Property;
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
    "CLT_StopRateFeature_Name=Stop Rate Feature",
    "CLT_StopRateFeature_Description=Determines the stop rate that occurrs within a gps track.",
    "CLT_StopRateFeature_Property_MinimumVelocityThreshold_Name=Minimum velocity Threshold",
    "CLT_StopRateFeature_Property_MinimumVelocityThreshold_Description=Parameter which specifies the minimum velocity value."
})
@ServiceProvider(service = FeatureProcess.class)
public class StopRateFeatureProcess extends FeatureProcess {

    private static final String PROP_NAME_MINIMUM_VELOCITY_THRESHOLD = "minimum.velocity.threshold";
    private static final Logger LOG = Logger.getLogger(StopRateFeatureProcess.class.getName());
    private TrackSegment trackSegement;
    private final StopRateFeature feature = new StopRateFeature();
    private Double minimumThreshold = null;

    public StopRateFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.setMinimumVelocityThreshold(getMinimumVelocityThreshold());
        feature.reset();
        if (trackSegement != null) {
            for (Waypoint waypoint : trackSegement.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        } else {
            throw new IllegalStateException("input data can not be null!");
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_StopRateFeature_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_StopRateFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.trackSegement = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        trackSegement = null;
        return result;
    }

    private double getMinimumVelocityThreshold() {
        if (minimumThreshold == null) {
            if (getProcessDescriptor() != null) {
                List<Property> propertyList = getProcessDescriptor().getProperties().getPropertyList();
                for (Property property : propertyList) {
                    if (PROP_NAME_MINIMUM_VELOCITY_THRESHOLD.equals(property.getId()) && property.getValue() != null) {
                        try {
                            minimumThreshold = Double.valueOf(property.getValue());
                        } catch (NumberFormatException ex) {
                            LOG.log(Level.WARNING, ex.getMessage(), ex);
                        }
                        break;
                    }
                }
            }
            if (minimumThreshold == null) {
                minimumThreshold = 3.4d;
            }
        }
        return minimumThreshold;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(StopRateFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_StopRateFeature_Name());
        descriptor.setDescription(Bundle.CLT_StopRateFeature_Description());

        Property property = new Property();
        property.setId(PROP_NAME_MINIMUM_VELOCITY_THRESHOLD);
        property.setJavaType(Double.class.getName());
        property.setValue("3.4");
        property.setName(Bundle.CLT_StopRateFeature_Property_MinimumVelocityThreshold_Name());
        property.setDescription(Bundle.CLT_StopRateFeature_Property_MinimumVelocityThreshold_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
