/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.AvgPrecisionFeature;
import de.fub.agg2graph.structs.GPSTrack;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgPrecisionFeature_Name=Average Precision",
    "CLT_AvgPrecisionFeature_Description=Feature measures the average precision of a gps track."
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgPrecisionFeatureProcess extends FeatureProcess {

    private final AvgPrecisionFeature feature = new AvgPrecisionFeature();
    private GPSTrack gpsTrack;

    @Override
    protected void start() {
        feature.reset();
    }

    @Override
    public String getName() {
        return Bundle.CLT_AvgPrecisionFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgPrecisionFeature_Description();
    }

    @Override
    public void setInput(GPSTrack gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
