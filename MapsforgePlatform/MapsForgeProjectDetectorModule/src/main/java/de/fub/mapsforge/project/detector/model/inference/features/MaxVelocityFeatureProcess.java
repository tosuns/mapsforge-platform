/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MaxNSpeedFeature;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MaxSpeedFeature_Name=Maximum Velocity",
    "CLT_MaxSpeedFeature_Description=Feature that computes the maximum Velocity that appears within a gps track."
})
@ServiceProvider(service = FeatureProcess.class)
public class MaxVelocityFeatureProcess extends FeatureProcess {

    private final MaxNSpeedFeature feature = new MaxNSpeedFeature(1);
    private TrackSegment gpsTrack;

    public MaxVelocityFeatureProcess() {
        super(null);
    }

    public MaxVelocityFeatureProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        feature.reset();
        for (Waypoint waypoint : gpsTrack.getWayPointList()) {
            feature.addWaypoint(waypoint);
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_MaxSpeedFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MaxSpeedFeature_Description();
    }

    @Override
    public void setInput(TrackSegment gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
