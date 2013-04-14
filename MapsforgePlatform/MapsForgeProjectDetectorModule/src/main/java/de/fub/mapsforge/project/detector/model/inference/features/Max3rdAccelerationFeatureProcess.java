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
    "CLT_Max3rdAccelerationFeature_Name=Third Maximal Acceleration",
    "CLT_Max3rdAccelerationFeature_Description=Feature that computes the thrid highest acceleration value which a track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class Max3rdAccelerationFeatureProcess extends FeatureProcess {

    private MaxNAccelerationFeature feature = new MaxNAccelerationFeature(3);
    private TrackSegment tracks = null;

    public Max3rdAccelerationFeatureProcess() {
        this(null);
    }

    public Max3rdAccelerationFeatureProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected void start() {
        feature.reset();
        for (Waypoint waypoint : tracks.getWayPointList()) {
            feature.addWaypoint(waypoint);
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_Max3rdAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_Max3rdAccelerationFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.tracks = input;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
