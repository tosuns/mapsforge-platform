/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.agg2graph.gpseval.features.AvgSpeedFeature;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AvgSpeedFeature_Name=Average Speed",
    "CLT_AvgSpeedFeature_Description=A feature for a classifier responsible to compotue the average speed of an GPS track."
})
@ServiceProvider(service = FeatureProcess.class)
public class AvgSpeedFeatureProcess extends FeatureProcess {

    private GPSTrack gpsTrack;
    private final AvgSpeedFeature feature = new AvgSpeedFeature();

    public AvgSpeedFeatureProcess() {
        this(null);
    }

    public AvgSpeedFeatureProcess(Detector detector) {
        super(detector);
        init();
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
        feature.reset();

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
    public void setInput(GPSTrack gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public Double getResult() {
        return feature.getResult();
    }
}
