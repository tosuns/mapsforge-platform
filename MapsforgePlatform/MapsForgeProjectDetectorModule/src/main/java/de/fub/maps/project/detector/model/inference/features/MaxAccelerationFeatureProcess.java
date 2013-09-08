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
import de.fub.agg2graph.gpseval.features.MaxNAccelerationFeature;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MaxAccelerationFeature_Name=Maximal Acceleration",
    "CLT_MaxAccelerationFeature_Description=Feature that computes the maximal acceleration which a gps track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class MaxAccelerationFeatureProcess extends FeatureProcess {

    private final MaxNAccelerationFeature feature = new MaxNAccelerationFeature(1);
    private TrackSegment gpxTrack;

    public MaxAccelerationFeatureProcess() {
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
        return Bundle.CLT_MaxAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MaxAccelerationFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.gpxTrack = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        gpxTrack = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MaxAccelerationFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_MaxAccelerationFeature_Name());
        descriptor.setDescription(Bundle.CLT_MaxAccelerationFeature_Description());
        return descriptor;
    }
}
