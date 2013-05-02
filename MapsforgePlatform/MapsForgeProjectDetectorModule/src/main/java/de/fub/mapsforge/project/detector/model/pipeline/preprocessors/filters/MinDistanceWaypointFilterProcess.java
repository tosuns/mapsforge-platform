/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.data.filter.MinDistanceWaypointFilter;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({"CLT_MinDIsFilter_Name=Minimum Distance Filter",
    "CLT_MinDisFilter_Description=This filter check a GPSTack whether each pair of gps "
    + "points exceeds the required minimum distance threshold. If a pair of gps point "
    + "can't satisfy the requirement, then the track will be can't into two segments and "
    + "the filter process continues with the filtering on the second segment until the end of the gps track is reached."})
@ServiceProvider(service = FilterProcess.class)
public class MinDistanceWaypointFilterProcess extends FilterProcess {

    private static final String PROPERTY_DISTANCE = "distance";
    private List<TrackSegment> gpsTracks;
    private final MinDistanceWaypointFilter filter = new MinDistanceWaypointFilter();

    public MinDistanceWaypointFilterProcess() {
        this(null);
    }

    public MinDistanceWaypointFilterProcess(Detector detector) {
        super(detector);
        init();
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
        return Bundle.CLT_MinDIsFilter_Name();
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
}
