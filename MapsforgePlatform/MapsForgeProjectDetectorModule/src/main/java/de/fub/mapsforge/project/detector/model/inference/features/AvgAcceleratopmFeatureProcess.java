/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.AvgAccelerationFeature;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.inference.FeatureProcess;

/**
 *
 * @author Serdar
 */
public class AvgAcceleratopmFeatureProcess extends FeatureProcess<GPSTrack, Double> {

    private final AvgAccelerationFeature feature = new AvgAccelerationFeature();
    private GPSTrack gpsTrack;

    @Override
    protected void start() {
        feature.reset();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setInput(GPSTrack gpstrack) {
        this.gpsTrack = gpstrack;
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
