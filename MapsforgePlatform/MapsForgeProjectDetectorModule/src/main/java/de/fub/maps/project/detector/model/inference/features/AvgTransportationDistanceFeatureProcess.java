/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.AvgTransportationDistanceFeature;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgTransportationDistanceFeature_Name=Average Transportation DIstance",
    "CLT_AvgTransportationDistanceFeature_Description=Feature that computes the average distance of public transportation stations to an gps track"
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgTransportationDistanceFeatureProcess extends FeatureProcess {

    private TrackSegment gpxTrack;
    private final AvgTransportationDistanceFeature feature = new AvgTransportationDistanceFeature();

    public AvgTransportationDistanceFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (gpxTrack != null) {
            for (Waypoint waypoint : gpxTrack.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_AvgTransportationDistanceFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgTransportationDistanceFeature_Description();
    }

    @Override
    public void setInput(TrackSegment gpxTrack) {
        this.gpxTrack = gpxTrack;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        this.gpxTrack = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(AvgTransportationDistanceFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_AvgTransportationDistanceFeature_Name());
        descriptor.setDescription(Bundle.CLT_AvgTransportationDistanceFeature_Description());
        return descriptor;
    }
}
