/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.AvgBearingChangeFeature;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgBearingChangeFeature_Name=Average Bearing Change",
    "CLT_AvgBearingChangeFeature_Description=Feature computes the average bearing change that occures within the gps track."
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgBearingChangeFeatureProcess extends FeatureProcess {

    private final AvgBearingChangeFeature feature = new AvgBearingChangeFeature();
    private TrackSegment gpsTrack;

    public AvgBearingChangeFeatureProcess() {
        super(null);
    }

    public AvgBearingChangeFeatureProcess(Detector detector) {
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
        return Bundle.CLT_AvgBearingChangeFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgBearingChangeFeature_Description();
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
