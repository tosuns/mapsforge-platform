/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.AvgTransportationDistanceFeature;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.inference.FeatureProcess;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgTransportationDistanceFeature_Name=Average Transportation DIstance",
    "CLT_AvgTransportationDistanceFeature_Description=Feature that computes the average distance of public transportation stations to an gps track"
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgTransportationDistanceFeatureProcess extends FeatureProcess<GPSTrack, Double> {

    private GPSTrack gpxTrack;
    private final AvgTransportationDistanceFeature feature = new AvgTransportationDistanceFeature();

    @Override
    protected void start() {
    }

    @Override
    public String getName() {
        return Bundle.CLT_AvgTransportationDistanceFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgTransportationDistanceFeature_Description();
    }

    @Override
    public void setInput(GPSTrack gpxTrack) {
        this.gpxTrack = gpxTrack;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }

    @Override
    public boolean cancel() {
        return false;
    }
}
