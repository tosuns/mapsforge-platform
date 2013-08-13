/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.data.filter.LimitWaypointFilter;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.List;
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
    + "second segment until it reaches the end of the track.",
    "CLT_LimitWaypointFilter_Property_Limit_Name=Limit",
    "CLT_LimitWaypointFilter_Property_Limit_Description=Maximal number of gps points."
})
@ServiceProvider(service = FilterProcess.class)
public class LimitWaypointFilterProcess extends FilterProcess {

    private final static String PROPERTY_LIMIT = "limit";
    private List<TrackSegment> gpxTracks;
    private final LimitWaypointFilter filter = new LimitWaypointFilter();

    public LimitWaypointFilterProcess() {
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
        init();
        filter.reset();
        for (TrackSegment trackSegment : gpxTracks) {
            ArrayList<Waypoint> arrayList = new ArrayList<Waypoint>(trackSegment.getWayPointList());
            for (Waypoint waypoint : arrayList) {
                if (!filter.filter(waypoint)) {
                    trackSegment.getWayPointList().remove(waypoint);
                }
            }
        }
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
    public void setInput(List<TrackSegment> gpsTrack) {
        this.gpxTracks = gpsTrack;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> segmentList = this.gpxTracks;
        this.gpxTracks = null;
        return segmentList;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(LimitWaypointFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_LimitWaypointFilter_Name());
        descriptor.setDescription(Bundle.CLT_LimitWaypointFilter_Description());

        Property property = new Property();
        property.setId(PROPERTY_LIMIT);
        property.setJavaType(Integer.class.getName());
        property.setValue("2");
        property.setName(Bundle.CLT_LimitWaypointFilter_Property_Limit_Name());
        property.setDescription(Bundle.CLT_LimitWaypointFilter_Property_Limit_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
