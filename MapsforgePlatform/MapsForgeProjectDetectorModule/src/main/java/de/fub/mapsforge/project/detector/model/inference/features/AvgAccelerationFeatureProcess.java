/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.AvgAccelerationFeature;
import de.fub.gpxmodule.xml.gpx.Gpx;
import java.util.List;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgAccelerationFeature_Name=Average Acceleration",
    "CLT_AvgAccelerationFeature_Description=Feature that computes the average acceleration which a gps track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgAccelerationFeatureProcess extends FeatureProcess {

    private final AvgAccelerationFeature feature = new AvgAccelerationFeature();
    private List<Gpx> gpsTrack;

    @Override
    protected void start() {
        feature.reset();
    }

    @Override
    public String getName() {
        return Bundle.CLT_AvgAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgAccelerationFeature_Description();
    }

    @Override
    public void setInput(List<Gpx> gpstrack) {
        this.gpsTrack = gpstrack;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
