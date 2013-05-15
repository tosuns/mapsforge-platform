/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.AvgVelocityFeature;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgSpeedFeature_Name=Average Velocity",
    "CLT_AvgSpeedFeature_Description=A feature for a classifier responsible to compotue the average velocity of an GPS track."
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgVelocityFeatureProcess extends FeatureProcess {

    private TrackSegment gpsTrack;
    private final AvgVelocityFeature feature = new AvgVelocityFeature();

    public AvgVelocityFeatureProcess() {
    }

    private void init() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property property : processDescriptor.getProperties().getPropertyList()) {
                feature.setParam(property.getName(), property.getValue());
            }
        }
    }

    @Override
    protected void start() {
        init();
        feature.reset();
        if (gpsTrack != null) {
            for (Waypoint waypoint : gpsTrack.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_AvgSpeedFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AvgSpeedFeature_Description();
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
}
