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
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_ResegmentationFilter_Name=Resegmentation Filter",
    "CLT_ResegmentationFilter_Description=This filter resegmentats gpx segments if it determins there are jumps in context of distance between tow point."
})
@ServiceProvider(service = FilterProcess.class)
public class ResegmentationFilterProcess extends FilterProcess {

    private List<TrackSegment> trackSegments;
    private List<TrackSegment> resultList = new ArrayList<TrackSegment>(200);
    private Waypoint lastPoint = null;
    private double averagDistance = 0;
    private double totalDistance = 0;
    private int pointCount = 0;

    public ResegmentationFilterProcess() {
    }

    @Override
    protected void start() {
        if (resultList == null) {
            resultList = new ArrayList<TrackSegment>();
        }
        TrackSegment subSegment = null;
        for (TrackSegment segment : trackSegments) {
            subSegment = new TrackSegment();
            pointCount = 0;
            totalDistance = 0;

            for (Waypoint waypoint : segment.getWayPointList()) {
                if (lastPoint != null) {
                    double distance = GPSCalc.getDistVincentyFast(waypoint.getLat(), waypoint.getLon(), lastPoint.getLat(), lastPoint.getLon());

                    totalDistance += distance;

                    averagDistance = totalDistance / pointCount;

                    if ((averagDistance * 2) < distance) {
                        resultList.add(subSegment);
                        subSegment = new TrackSegment();
                        pointCount = 0;
                        totalDistance = 0;
                    }
                }

                subSegment.add(waypoint);
                lastPoint = waypoint;
                pointCount++;
            }

            if (!resultList.contains(subSegment)) {
                resultList.add(subSegment);
            }
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_ResegmentationFilter_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_ResegmentationFilter_Description();
    }

    @Override
    public void setInput(List<TrackSegment> input) {
        this.trackSegments = input;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> arrayList = this.resultList;
        this.resultList = null;
        this.trackSegments = null;
        return arrayList;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(ResegmentationFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_ResegmentationFilter_Name());
        descriptor.setDescription(Bundle.CLT_ResegmentationFilter_Description());
        return descriptor;
    }
}
