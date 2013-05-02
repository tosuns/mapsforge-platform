/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.TrackLengthFeature;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_TrackLengthFeature_Name=Track Length Feature",
    "CLT_TrackLengthFeature_Description=Computes the length of a given track as feature."
})
@ServiceProvider(service = FeatureProcess.class)
public class TrackLengthFeatureProcess extends FeatureProcess {

    private TrackLengthFeature feature = new TrackLengthFeature();
    private TrackSegment track;

    public TrackLengthFeatureProcess() {
        this(null);
    }

    public TrackLengthFeatureProcess(Detector detector) {
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
        return Bundle.CLT_TrackLengthFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_TrackLengthFeature_Description();
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
