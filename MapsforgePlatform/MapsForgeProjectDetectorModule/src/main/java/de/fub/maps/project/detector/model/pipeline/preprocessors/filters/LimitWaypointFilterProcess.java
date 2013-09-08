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
