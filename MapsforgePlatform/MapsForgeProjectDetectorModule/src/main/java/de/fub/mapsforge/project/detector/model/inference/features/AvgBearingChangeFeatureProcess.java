/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.AvgBearingChangeFeature;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.inference.FeatureProcess;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgBearingChangeFeature_Name=Average Bearing Change Feature",
    "CLT_AvgBearingChangeFeature_Description=Feature computes the average bearing change that occures within the gps track."
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgBearingChangeFeatureProcess extends FeatureProcess<GPSTrack, Double> {

    private final AvgBearingChangeFeature feature = new AvgBearingChangeFeature();
    private GPSTrack gpsTack;

    @Override
    protected void start() {
        feature.reset();

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
    public void setInput(GPSTrack gpsTrack) {
        this.gpsTack = gpsTrack;
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
