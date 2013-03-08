/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.MaxAccelerationFeature;
import de.fub.agg2graph.structs.GPSTrack;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MaxAccelerationFeature_Name=Maximal Acceleration Feature",
    "CLT_MaxAccelerationFeature_Description=Feature that computes the maximal acceleration which a gps track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class MaxAccelerationFeatureProcess extends FeatureProcess {

    private final MaxAccelerationFeature feature = new MaxAccelerationFeature();
    private GPSTrack gpxTrack;

    @Override
    protected void start() {
        feature.reset();
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
    public void setInput(GPSTrack input) {
        this.gpxTrack = input;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
