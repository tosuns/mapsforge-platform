/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.StopRateFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.xmls.Property;
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
    "CLT_StopRateFeature_Description=Determines the stop rate that occurrs within a gps track."
})
@ServiceProvider(service = FeatureProcess.class)
public class StopRateFeatureProcess extends FeatureProcess {

    private static final String PROP_NAME_MINIMUM_VELOCITY_THRESHOLD = "minimum.velocity.threshold";
    private static final Logger LOG = Logger.getLogger(StopRateFeatureProcess.class.getName());
    private TrackSegment trackSegement;
    private StopRateFeature feature = new StopRateFeature();
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
}
