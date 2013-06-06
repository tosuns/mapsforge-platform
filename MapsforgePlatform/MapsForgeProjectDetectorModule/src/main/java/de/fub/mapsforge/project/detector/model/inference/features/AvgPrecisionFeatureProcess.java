/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.AvgPrecisionFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
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
    private TrackSegment gpsTrack;

    public AvgPrecisionFeatureProcess() {
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
        return Bundle.CLT_AvgPrecisionFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgPrecisionFeature_Description();
    }

    @Override
    public void setInput(TrackSegment gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        this.gpsTrack = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(AvgPrecisionFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_AvgPrecisionFeature_Name());
        descriptor.setDescription(Bundle.CLT_AvgPrecisionFeature_Description());
        return descriptor;
    }
}
