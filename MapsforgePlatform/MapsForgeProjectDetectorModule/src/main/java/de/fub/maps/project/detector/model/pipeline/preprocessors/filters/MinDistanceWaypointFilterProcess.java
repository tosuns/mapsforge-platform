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
import de.fub.agg2graph.gpseval.data.filter.MinDistanceWaypointFilter;
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
@NbBundle.Messages({"CLT_MinDisFilter_Name=Minimum Distance Filter",
    "CLT_MinDisFilter_Description=This filter check a GPSTack whether each pair of gps "
    + "points exceeds the required minimum distance threshold. If a pair of gps point "
    + "can't satisfy the requirement, then the track will be can't into two segments and "
    + "the filter process continues with the filtering on the second segment until the end of the gps track is reached.",
    "CLT_MinDisFilter_Property_Distance_Name=Minimum Distance",
    "CLT_MinDisFilter_Property_Distance_Description=Minimum distance in meters, which a track has to display."
})
@ServiceProvider(service = FilterProcess.class)
public class MinDistanceWaypointFilterProcess extends FilterProcess {

    private static final String PROPERTY_DISTANCE = "distance";
    private List<TrackSegment> gpsTracks;
    private final MinDistanceWaypointFilter filter = new MinDistanceWaypointFilter();

    public MinDistanceWaypointFilterProcess() {
    }

    private void init() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property property : processDescriptor.getProperties().getPropertyList()) {
                filter.setParam(PROPERTY_DISTANCE, property.getValue());
            }
        }
    }

    @Override
    protected void start() {
        init();
        filter.reset();
        for (TrackSegment trackSegment : gpsTracks) {
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
        return Bundle.CLT_MinDisFilter_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MinDisFilter_Description();
    }

    @Override
    public void setInput(List<TrackSegment> gpsTrack) {
        this.gpsTracks = gpsTrack;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> list = gpsTracks;
        gpsTracks = null;
        return list;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MinDistanceWaypointFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_MinDisFilter_Name());
        descriptor.setDescription(Bundle.CLT_MinDisFilter_Description());

        // <!-- value in meters -->
        Property property = new Property();
        property.setId(PROPERTY_DISTANCE);
        property.setJavaType(Integer.class.getName());
        property.setValue("2");
        property.setName(Bundle.CLT_MinDisFilter_Property_Distance_Name());
        property.setDescription(Bundle.CLT_MinDisFilter_Property_Distance_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
