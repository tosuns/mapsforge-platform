/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.HeadingChangeRateFeature;
import de.fub.mapsforge.project.detector.model.Detector;
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
    "CLT_HeadingChangeRateFeature_Name=Heading Change Rate Feature",
    "CLT_HeadingChangeRateFeature_Description=Computes the heading change rate of a gps segment. The heading change rate is determined via a threashold parameter."
})
@ServiceProvider(service = FeatureProcess.class)
public class HeadingChangeRateFeatureProcess extends FeatureProcess {

    private static final Logger LOG = Logger.getLogger(HeadingChangeRateFeatureProcess.class.getName());
    private static final String PROP_NAME_ANGLE_THRESHOLD = "minimum.angle.threshold";
    private Double minimumAngleThreshold = null;
    private HeadingChangeRateFeature feature = new HeadingChangeRateFeature();
    private TrackSegment trackSegment;

    public HeadingChangeRateFeatureProcess() {
        this(null);
    }

    public HeadingChangeRateFeatureProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        feature.reset();
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
}
