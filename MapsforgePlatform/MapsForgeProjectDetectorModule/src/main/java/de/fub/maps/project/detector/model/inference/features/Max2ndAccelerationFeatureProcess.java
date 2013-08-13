/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.MaxNAccelerationFeature;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Max2ndAccelerationFeature_Name=Second Maximal Acceleration",
    "CLT_Max2ndAccelerationFeature_Description=Feature that computes the second highest acceleration value which a track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class Max2ndAccelerationFeatureProcess extends FeatureProcess {

    private MaxNAccelerationFeature feature = new MaxNAccelerationFeature(2);
    private TrackSegment tracks;

    public Max2ndAccelerationFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (tracks != null) {
            for (Waypoint waypoint : tracks.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_Max2ndAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_Max2ndAccelerationFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.tracks = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        this.tracks = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(Max2ndAccelerationFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_Max2ndAccelerationFeature_Name());
        descriptor.setDescription(Bundle.CLT_Max2ndAccelerationFeature_Description());
        return descriptor;
    }
}
