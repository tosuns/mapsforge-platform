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
    "CLT_Max3rdAccelerationFeature_Name=Third Maximal Acceleration",
    "CLT_Max3rdAccelerationFeature_Description=Feature that computes the thrid highest acceleration value which a track contains."
})
@ServiceProvider(service = FeatureProcess.class)
public class Max3rdAccelerationFeatureProcess extends FeatureProcess {

    private final MaxNAccelerationFeature feature = new MaxNAccelerationFeature(3);
    private TrackSegment tracks = null;

    public Max3rdAccelerationFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (tracks != null) {
            for (Waypoint waypoint : tracks.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_Max3rdAccelerationFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_Max3rdAccelerationFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.tracks = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        tracks = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(Max3rdAccelerationFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_Max3rdAccelerationFeature_Name());
        descriptor.setDescription(Bundle.CLT_Max3rdAccelerationFeature_Description());
        return descriptor;
    }
}
