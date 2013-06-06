/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MaxNVelocityFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Max2ndVelocityFeature_Name=Second Maximal Velocity",
    "CLT_Max2ndVelocityFeature_Description=Feature that computes the second highest Velocity value which a track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class Max2ndVelocityFeatureProcess extends FeatureProcess {

    private final MaxNVelocityFeature feature = new MaxNVelocityFeature(2);
    private TrackSegment gpsTrack;

    public Max2ndVelocityFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (gpsTrack != null) {
            for (Waypoint waypoint : gpsTrack.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_Max2ndVelocityFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_Max2ndVelocityFeature_Description();
    }

    @Override
    public void setInput(TrackSegment gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        gpsTrack = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(Max2ndVelocityFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_Max2ndVelocityFeature_Name());
        descriptor.setDescription(Bundle.CLT_Max2ndVelocityFeature_Description());
        return descriptor;
    }
}
