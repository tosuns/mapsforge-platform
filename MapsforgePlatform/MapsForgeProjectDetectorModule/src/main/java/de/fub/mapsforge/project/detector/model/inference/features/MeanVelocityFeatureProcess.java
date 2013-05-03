/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MeanVelocityFeature;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
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
        this(null);
    }

    public MeanVelocityFeatureProcess(Detector detector) {
        super(detector);
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
}
