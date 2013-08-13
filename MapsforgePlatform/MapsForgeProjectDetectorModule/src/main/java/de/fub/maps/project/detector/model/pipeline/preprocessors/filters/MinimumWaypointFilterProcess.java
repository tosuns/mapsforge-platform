/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.pipeline.preprocessors.filters;

import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MinimumWaypointFilter_Name=Minimum Waypoints Filter",
    "CLT_MinimumWaypointFilter_Description=This Process filters all track segment whose don't contain the amount of waypoint specified as parameter.",
    "CLT_MinimumWaypointFilter_Property_MinWaypointCount_Name=Minimum Waypoint Count",
    "CLT_MinimumWaypointFilter_Property_MinWaypointCount_Description=Specifies the minimum amount of waypoint a track segment must contain."
})
@ServiceProvider(service = FilterProcess.class)
public class MinimumWaypointFilterProcess extends FilterProcess {

    private static final String PROP_NAME_MIN_WAYPOINT_COUNT = "min.way.point.count";
    private List<TrackSegment> trackSegments;
    private Integer minWaypointCount = null;

    public MinimumWaypointFilterProcess() {
    }

    @Override
    protected void start() {
        for (TrackSegment trackSegment : new ArrayList<TrackSegment>(trackSegments)) {
            if (trackSegment.getWayPointList().size() < getMinWaypointCount()) {
                trackSegments.remove(trackSegment);
            }
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_MinimumWaypointFilter_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_MinimumWaypointFilter_Description();
    }

    @Override
    public void setInput(List<TrackSegment> input) {
        this.trackSegments = input;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> list = trackSegments;
        trackSegments = null;
        return list;
    }

    private Integer getMinWaypointCount() {
        if (minWaypointCount == null) {
            if (getProcessDescriptor() != null) {
                List<Property> propertyList = getProcessDescriptor().getProperties().getPropertyList();
                for (Property property : propertyList) {
                    if (PROP_NAME_MIN_WAYPOINT_COUNT.equals(property.getId()) && property.getValue() != null) {
                        try {
                            minWaypointCount = Integer.parseInt(property.getValue());
                        } catch (NumberFormatException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        break;
                    }
                }
            }
            if (minWaypointCount == null) {
                minWaypointCount = 50;
            }
        }
        return minWaypointCount;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MinimumWaypointFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_MinimumWaypointFilter_Name());
        descriptor.setDescription(Bundle.CLT_MinimumWaypointFilter_Description());

        Property property = new Property();
        property.setId(PROP_NAME_MIN_WAYPOINT_COUNT);
        property.setJavaType(Integer.class.getName());
        property.setValue("50");
        property.setName(Bundle.CLT_MinimumWaypointFilter_Property_MinWaypointCount_Name());
        property.setDescription(Bundle.CLT_MinimumWaypointFilter_Property_MinWaypointCount_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
