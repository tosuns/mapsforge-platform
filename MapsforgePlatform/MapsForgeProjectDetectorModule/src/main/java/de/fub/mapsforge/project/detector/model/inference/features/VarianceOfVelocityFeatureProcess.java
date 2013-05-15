/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.VarianceOfVelocityFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_VarianceOfVelocityFeature_Name=Variance Of Velocity Feature",
    "CLT_VarianceOfVelocityFeature_Description=Computss the variance of the velocity which a gps track contrains."
})
@ServiceProvider(service = FeatureProcess.class)
public class VarianceOfVelocityFeatureProcess extends FeatureProcess {

    private final VarianceOfVelocityFeature feature = new VarianceOfVelocityFeature();
    private TrackSegment track;

    public VarianceOfVelocityFeatureProcess() {
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
        return Bundle.CLT_VarianceOfVelocityFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_VarianceOfVelocityFeature_Description();
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
}
