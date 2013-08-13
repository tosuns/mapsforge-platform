/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.data.filter.MinTimeDiffWaypointFilter;
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
    "CLT_MinTimeDiffWaypointFilterProcess_Name=Minimum Time Difference Filter",
    "CLT_MinTimeDiffWaypointFilterProcess_Description=This filter is responsible "
    + "to check whether the time difference between each pair of gps points "
    + "contained by a gps track does not exceed a given threshold. if there "
    + "is a pair of gps point where the time difference exceed the specified "
    + "the track get seperated into two segement and the filter continues "
    + "the filtering process with the second segment until the end of the "
    + "gps track is reached.",
    "CLT_MinTimeDiffWaypointFilterProcess_Property_TimeDiff_Name=Time diffenrence",
    "CLT_MinTimeDiffWaypointFilterProcess_Property_TimeDiff_Description=Time difference in seconds"
})
@ServiceProvider(service = FilterProcess.class)
public class MinTimeDiffWaypointFilterProcess extends FilterProcess {

    private static final String PROPERTY_TIME_DIFF = "timeDiff";
    private List<TrackSegment> gpxTracks = null;
    private MinTimeDiffWaypointFilter filter = new MinTimeDiffWaypointFilter();

    public MinTimeDiffWaypointFilterProcess() {
    }

    private void init() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property property : processDescriptor.getProperties().getPropertyList()) {
                filter.setParam(PROPERTY_TIME_DIFF, property.getValue());
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
                    trackSegment.remove(waypoint);
                }
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_MinTimeDiffWaypointFilterProcess_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MinTimeDiffWaypointFilterProcess_Description();
    }

    @Override
    public void setInput(List<TrackSegment> gpsTrack) {
        this.gpxTracks = gpsTrack;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> list = this.gpxTracks;
        this.gpxTracks = null;
        return list;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MinTimeDiffWaypointFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_MinTimeDiffWaypointFilterProcess_Name());
        descriptor.setDescription(Bundle.CLT_MinTimeDiffWaypointFilterProcess_Description());

        Property property = new Property();
        property.setId(PROPERTY_TIME_DIFF);
        property.setJavaType(Integer.class.getName());
        property.setValue("2");
        property.setName(Bundle.CLT_MinTimeDiffWaypointFilterProcess_Property_TimeDiff_Name());
        property.setDescription(Bundle.CLT_MinTimeDiffWaypointFilterProcess_Property_TimeDiff_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
