/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import java.util.AbstractList;
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

    public ResegmentationFilterProcess(Detector detector) {
        super(detector);
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
}
