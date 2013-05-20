/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MaxNAccelerationFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MaxAccelerationFeature_Name=Maximal Acceleration",
    "CLT_MaxAccelerationFeature_Description=Feature that computes the maximal acceleration which a gps track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class MaxAccelerationFeatureProcess extends FeatureProcess {

    private final MaxNAccelerationFeature feature = new MaxNAccelerationFeature(1);
    private TrackSegment gpxTrack;

    public MaxAccelerationFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (gpxTrack != null) {
            for (Waypoint waypoint : gpxTrack.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_MaxAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MaxAccelerationFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.gpxTrack = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        gpxTrack = null;
        return result;
    }
}
