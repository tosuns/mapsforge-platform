/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.MinPrecisionFeature;
import de.fub.agg2graph.structs.GPSTrack;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MinPrecisionFeature_Name=Minimum Precision",
    "CLT_MinPrecisionFeature_Description=Feature computes the minimum of precision that occures within a gps track."
})
@ServiceProvider(service = FeatureProcess.class)
public class MinPrecisionFeatureProcess extends FeatureProcess {

    private MinPrecisionFeature feature = new MinPrecisionFeature();
    private GPSTrack gpsTrack;

    @Override
    protected void start() {
        feature.reset();
    }

    @Override
    public String getName() {
        return Bundle.CLT_MinPrecisionFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MinPrecisionFeature_Description();
    }

    @Override
    public void setInput(GPSTrack input) {
        this.gpsTrack = input;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
