/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.Property;
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
    "CLT_MinimumWaypointFilter_Description=This Process filters all track segment whose don't contain the amount of waypoint specified as parameter."
})
@ServiceProvider(service = FilterProcess.class)
public class MinimumWaypointFilterProcess extends FilterProcess {

    private static final String PROP_NAME_MIN_WAYPOINT_COUNT = "min.way.point.count";
    private List<TrackSegment> trackSegments;
    private Integer minWaypointCount = null;

    public MinimumWaypointFilterProcess() {
    }

    public MinimumWaypointFilterProcess(Detector detector) {
        super(detector);
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
        return trackSegments;
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
}
