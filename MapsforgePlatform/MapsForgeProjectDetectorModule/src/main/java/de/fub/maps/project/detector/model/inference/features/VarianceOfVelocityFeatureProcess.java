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
import de.fub.agg2graph.gpseval.features.VarianceOfVelocityFeature;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_VarianceOfVelocityFeature_Name=Variance Of Velocity Feature",
    "CLT_VarianceOfVelocityFeature_Description=Computss the variance of the velocity which a gps track contrains."
})
@ServiceProvider(service = FeatureProcess.class)
public class VarianceOfVelocityFeatureProcess extends FeatureProcess {

    private final VarianceOfVelocityFeature feature = new VarianceOfVelocityFeature();
    private TrackSegment track;

    public VarianceOfVelocityFeatureProcess() {
    }

    @Override
    protected void start() {
        feature.reset();
        if (track != null) {
            for (Waypoint waypoint : track.getWayPointList()) {
                feature.addWaypoint(waypoint);
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_VarianceOfVelocityFeature_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_VarianceOfVelocityFeature_Description();
    }

    @Override
    public void setInput(TrackSegment input) {
        this.track = input;
    }

    @Override
    public Double getResult() {
        double result = feature.getResult();
        feature.reset();
        track = null;
        return result;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(VarianceOfVelocityFeatureProcess.class.getName());
        descriptor.setName(Bundle.CLT_VarianceOfVelocityFeature_Name());
        descriptor.setDescription(Bundle.CLT_VarianceOfVelocityFeature_Description());
        return descriptor;
    }
}
