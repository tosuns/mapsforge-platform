/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MaxNAccelerationFeature;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Max2ndAccelerationFeature_Name=Second Maximal Acceleration",
    "CLT_Max2ndAccelerationFeature_Description=Feature that computes the second highest acceleration value which a track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class Max2ndAccelerationFeatureProcess extends FeatureProcess {

    private MaxNAccelerationFeature feature = new MaxNAccelerationFeature(2);
    private TrackSegment tracks;

    public Max2ndAccelerationFeatureProcess() {
        this(null);
    }

    public Max2ndAccelerationFeatureProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        feature.reset();
        if (tracks != null) {
            for (Waypoint waypoint : tracks.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_Max2ndAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_Max2ndAccelerationFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.tracks = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        this.tracks = null;
        return result;
    }
}
