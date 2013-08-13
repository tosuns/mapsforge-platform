/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.HeadingChangeRateFeature;
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
    "CLT_HeadingChangeRateFeature_Name=Heading Change Rate Feature",
    "CLT_HeadingChangeRateFeature_Description=Computes the heading change rate of a gps segment. The heading change rate is determined via a threashold parameter.",
    "CLT_HeadingChangeRateFeature_Property_AngleThreshold_Name=Angle Threshold",
    "CLT_HeadingChangeRateFeature_Property_AngleThreshold_Description=If the heading of a gps point exceeds this value (in degree), then the point will be used for the heading change rate of the segment."
})
@ServiceProvider(service = FeatureProcess.class)
public class HeadingChangeRateFeatureProcess extends FeatureProcess {

    private static final Logger LOG = Logger.getLogger(HeadingChangeRateFeatureProcess.class.getName());
    private static final String PROP_NAME_ANGLE_THRESHOLD = "minimum.angle.threshold";
    private Double minimumAngleThreshold = null;
    private HeadingChangeRateFeature feature = new HeadingChangeRateFeature();
    private TrackSegment trackSegment;

    public HeadingChangeRateFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        feature.setHeadingThreshold(getAngleThreshold());
        if (trackSegment != null) {
            for (Waypoint waypoint : trackSegment.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        } else {
            throw new IllegalStateException("input can not be null!"); // NOI18N
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_HeadingChangeRateFeature_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_HeadingChangeRateFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.trackSegment = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        this.trackSegment = null;
        return result;
    }

    private Double getAngleThreshold() {
        if (minimumAngleThreshold == null) {
            if (getProcessDescriptor() != null) {
                List<Property> propertyList = getProcessDescriptor().getProperties().getPropertyList();
                for (Property property : propertyList) {
                    if (PROP_NAME_ANGLE_THRESHOLD.equals(property.getId()) && property.getValue() != null) {
                        try {
                            minimumAngleThreshold = Double.valueOf(property.getValue());
                            if (minimumAngleThreshold < 0 || minimumAngleThreshold > 360) {
                                minimumAngleThreshold = null;
                            }
                        } catch (NumberFormatException ex) {
                            LOG.log(Level.WARNING, ex.getMessage(), ex);
                        }
                        break;
                    }
                }
            }

            if (minimumAngleThreshold == null) {
                minimumAngleThreshold = 19d;
            }

        }
        return minimumAngleThreshold;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(HeadingChangeRateFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_HeadingChangeRateFeature_Name());
        descriptor.setDescription(Bundle.CLT_HeadingChangeRateFeature_Description());

        Property property = new Property();
        property.setId(PROP_NAME_ANGLE_THRESHOLD);
        property.setJavaType(Double.class.getName());
        property.setValue("19");
        property.setName(Bundle.CLT_HeadingChangeRateFeature_Property_AngleThreshold_Name());
        property.setDescription(Bundle.CLT_HeadingChangeRateFeature_Property_AngleThreshold_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
