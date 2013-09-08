/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    private final MinTimeDiffWaypointFilter filter = new MinTimeDiffWaypointFilter();

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
