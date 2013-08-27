/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MaxPrecisionFeature;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
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
    private TrackSegment gpsTrack;

    public MaxPrecisionFeatureProcess() {
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
        return Bundle.CLT_MaxPrecisionFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MaxPrecisionFeature_Description();
    }

    @Override
    public void setInput(TrackSegment gpsTrack) {
        this.gpsTrack = gpsTrack;
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
        descriptor.setJavaType(MaxPrecisionFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_MaxPrecisionFeature_Name());
        descriptor.setDescription(Bundle.CLT_MaxPrecisionFeature_Description());
        return descriptor;
    }
}