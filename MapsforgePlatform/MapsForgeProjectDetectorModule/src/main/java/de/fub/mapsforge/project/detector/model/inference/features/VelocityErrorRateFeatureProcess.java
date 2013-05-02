/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.ErrorRateFeature;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_VelocityErrorRateFeature_Name=Velocity Error Rate",
    "CLT_VelocityErrorRateFeature_Description=Calculates the error rate concerning the velocity data that each track segment contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class VelocityErrorRateFeatureProcess extends FeatureProcess {

    private ErrorRateFeature feature = new ErrorRateFeature();
    private TrackSegment trackSegment;

    public VelocityErrorRateFeatureProcess() {
        super(null);
    }

    public VelocityErrorRateFeatureProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        feature.reset();
        for (Waypoint waypoint : trackSegment.getWayPointList()) {
            feature.addWaypoint(waypoint);
        }
        trackSegment = null;
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_VelocityErrorRateFeature_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_VelocityErrorRateFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.trackSegment = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        return result;
    }
}
