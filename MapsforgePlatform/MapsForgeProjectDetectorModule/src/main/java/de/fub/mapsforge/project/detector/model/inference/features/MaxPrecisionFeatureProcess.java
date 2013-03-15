/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.MaxPrecisionFeature;
import de.fub.gpxmodule.xml.gpx.Gpx;
import java.util.List;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MaxPrecisionFeature_Name=Maximum Precision",
    "CLT_MaxPrecisionFeature_Description=Feature that computes the maximum precision which occurs within a gps track."
})
@ServiceProvider(service = FeatureProcess.class)
public class MaxPrecisionFeatureProcess extends FeatureProcess {

    private MaxPrecisionFeature feature = new MaxPrecisionFeature();
    private List<Gpx> gpsTrack;

    @Override
    protected void start() {
        feature.reset();
    }

    @Override
    public String getName() {
        return Bundle.CLT_MaxPrecisionFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MaxPrecisionFeature_Description();
    }

    @Override
    public void setInput(List<Gpx> gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
