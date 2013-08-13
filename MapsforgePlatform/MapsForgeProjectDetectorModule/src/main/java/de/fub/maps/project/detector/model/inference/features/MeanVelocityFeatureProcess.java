/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MeanVelocityFeature;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MeanVelocityFeature_Name=Mean Velocity Feature",
    "CLT_MeanVelocityFeature_Description=Computes the mean velocity from a gps track"
})
@ServiceProvider(service = FeatureProcess.class)
public class MeanVelocityFeatureProcess extends FeatureProcess {

    private final MeanVelocityFeature feature = new MeanVelocityFeature();
    private TrackSegment track;

    public MeanVelocityFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (track != null) {
            for (Waypoint waypoint : track.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_MeanVelocityFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MeanVelocityFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.track = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        track = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MeanVelocityFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_MeanVelocityFeature_Name());
        descriptor.setDescription(Bundle.CLT_MeanVelocityFeature_Description());
        return descriptor;

    }
}
