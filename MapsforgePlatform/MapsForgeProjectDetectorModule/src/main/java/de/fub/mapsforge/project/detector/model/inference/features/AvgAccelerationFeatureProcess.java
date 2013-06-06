/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.AvgAccelerationFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
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
    private TrackSegment gpsTrack;

    public AvgAccelerationFeatureProcess() {
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
        return Bundle.CLT_AvgAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgAccelerationFeature_Description();
    }

    @Override
    public void setInput(TrackSegment gpstrack) {
        this.gpsTrack = gpstrack;
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
        descriptor.setJavaType(AvgAccelerationFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_AvgAccelerationFeature_Name());
        descriptor.setDescription(Bundle.CLT_AvgAccelerationFeature_Description());
        return descriptor;
    }
}
