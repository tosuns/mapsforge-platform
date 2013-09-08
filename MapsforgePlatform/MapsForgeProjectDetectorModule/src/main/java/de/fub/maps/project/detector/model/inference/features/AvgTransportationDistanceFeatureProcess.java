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
