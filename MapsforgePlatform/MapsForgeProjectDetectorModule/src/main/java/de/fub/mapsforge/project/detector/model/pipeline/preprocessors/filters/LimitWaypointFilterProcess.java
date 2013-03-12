/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.filter.LimitWaypointFilter;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_LimitWaypointFilter_Name=Limit Waypoints Filter",
    "CLT_LimitWaypointFilter_Description=THis filter is responsible to check the "
    + "number of gps points that a gps track contains. If the number of gps "
    + "points exceeds a given threshold, then the track gets seperated into "
    + "two segments and the filter continous the filtering process with the "
    + "second segment until it reaches the end of the track."
})
@ServiceProvider(service = FilterProcess.class)
public class LimitWaypointFilterProcess extends FilterProcess<GPSTrack, GPSTrack> {

    private final static String PROPERTY_LIMIT = "limit";
    private GPSTrack gpsTrack;
    private final LimitWaypointFilter filter = new LimitWaypointFilter();

    public LimitWaypointFilterProcess() {
        this(null);
    }

    public LimitWaypointFilterProcess(Detector detector) {
        super(detector);
        init();
    }

    private void init() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property property : processDescriptor.getProperties().getPropertyList()) {
                filter.setParam(property.getName(), property.getValue());
            }
        }
    }

    @Override
    protected void start() {
        filter.reset();
        GPSTrack gpsTrack;
    }

    @Override
    public String getName() {
        return Bundle.CLT_LimitWaypointFilter_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_LimitWaypointFilter_Description();
    }

    @Override
    public void setInput(GPSTrack gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public GPSTrack getResult() {
        return this.gpsTrack;
    }

    @Override
    public boolean cancel() {
        return false;
    }
}
