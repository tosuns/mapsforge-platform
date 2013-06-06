/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MinPrecisionFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
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
    private TrackSegment gpsTrack;

    public MinPrecisionFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (gpsTrack != null) {
            for (Waypoint waypoint : gpsTrack.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
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
    public void setInput(TrackSegment input) {
        this.gpsTrack = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        gpsTrack = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MinPrecisionFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_MinPrecisionFeature_Name());
        descriptor.setDescription(Bundle.CLT_MinPrecisionFeature_Description());
        return descriptor;
    }
}
