/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.model.gpx.GpxWayPoint;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of the Change-Point Segmentation algorithm of "Understanding
 * Transportation Modes Based on GPS Data for Web Applications" by YU ZHENG,
 * YUKUN CHEN, QUANNAN LI, XING XIE and WEI-YING MA
 *
 * @author Serdar
 */
@ServiceProvider(service = FilterProcess.class)
public class ChangePointSegmentationFilterProcess extends FilterProcess {

    private static final String PROP_NAME_LOOSE_UPPER_VELOCITY_BOUND = "change.point.upper.velocity.bound";
    private static final String PROP_NAME_LOOSE_UPPER_ACCELERATION_BOUND = "change.point.upper.acceleration.bound";
    private static final String PROP_NAME_CERTAIN_SEGMENT_LENGTH_THRESHOLD = "change.point.certain.seg.length.threshold";
    private static final String PROP_NAME_UNCERTAIN_SEGMENT_COUNT_THRESHOLD = "change.point.uncertain.seg.count.threshold";
    private static final String PROP_NAME_MINIMAL_DISTANCE_BOUND = "change.point.minimal.distance.bound";
    private static final String PROP_NAME_MINIMAL_TIME_DIFFERENCE = "change.point.minimal.time.difference";
    private static final Logger LOG = Logger.getLogger(ChangePointSegmentationFilterProcess.class.getName());
    private List<TrackSegment> result = new ArrayList<TrackSegment>(100);
    private List<TrackSegment> gpsTracks;
    // upper bounds for distingush walking and non-walking segment
    private double looseUpperBoundsVelocity = -1;
    private double looseUpperBoundsAcceleration = -1;
    // threshold that declareds that each segment's length exceeds this threshold as certain.
    private double certainSegmentLengthThreshold = -1;
    // threshold count if uncertain segment reach this count it will be declared as non-walking segment
    private int uncertainSegCountThreshold = -1;
    private long minimalTimeDifference = -1;
    private double minimalDistanceBound = -1;

    public ChangePointSegmentationFilterProcess() {
    }

    private void init() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            List<Property> propertyList = processDescriptor.getProperties().getPropertyList();
            try {
                for (Property property : propertyList) {
                    if (property.getValue() != null) {
                        if (PROP_NAME_CERTAIN_SEGMENT_LENGTH_THRESHOLD.equals(property.getId())) {
                            certainSegmentLengthThreshold = Double.valueOf(property.getValue());
                            continue;
                        } else if (PROP_NAME_LOOSE_UPPER_ACCELERATION_BOUND.equals(property.getId())) {
                            looseUpperBoundsAcceleration = Double.valueOf(property.getValue());
                            continue;
                        } else if (PROP_NAME_LOOSE_UPPER_VELOCITY_BOUND.equals(property.getId())) {
                            looseUpperBoundsVelocity = Double.valueOf(property.getValue());
                            continue;
                        } else if (PROP_NAME_UNCERTAIN_SEGMENT_COUNT_THRESHOLD.equals(property.getId())) {
                            uncertainSegCountThreshold = Integer.parseInt(property.getValue());
                            continue;
                        } else if (PROP_NAME_MINIMAL_DISTANCE_BOUND.equals(property.getId())) {
                            minimalDistanceBound = Double.valueOf(property.getValue());
                            continue;
                        } else if (PROP_NAME_MINIMAL_TIME_DIFFERENCE.equals(property.getId())) {
                            minimalTimeDifference = Long.valueOf(property.getValue());
                            continue;
                        } else if (PROP_NAME_FILTER_SCOPE.equals(property.getId())) {
                            try {
                                scope = InferenceMode.fromValue(property.getValue());
                            } catch (IllegalArgumentException ex) {
                                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                            }
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                setProcessState(ProcessState.SETTING_ERROR);
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected void start() {
        init();

        if (result == null) {
            result = new ArrayList<TrackSegment>();
        }
        result.clear();

        if (gpsTracks != null) {
            for (TrackSegment trackSegment : gpsTracks) {

                // 1. step determine (non) walking points
                List<ChangePointWaypoint> waypoints = transformTo(trackSegment);

                // 2. step partition into walkin / non-wakling segments.
                List<ChangePointSegment> partition = partionWaypoints(waypoints);

                // 3. step make segments certain
                List<ChangePointSegment> certainPartition = makeCertainParition(partition);

                // 4. step partion original segment with the help of the change point in the
                // walking segment in the certainPartition list.
                result.addAll(partitionTrackSegment(trackSegment, certainPartition));
            }
            gpsTracks.clear();
        }
    }

    private Collection<? extends TrackSegment> partitionTrackSegment(TrackSegment trackSegment, List<ChangePointSegment> certainPartition) {
        List<TrackSegment> partitionedTrackSegments = new ArrayList<TrackSegment>(certainPartition.size());

        for (ChangePointSegment changePointSegment : certainPartition) {

            if (changePointSegment.getTransportMode() == Type.WALKING) {
                if (!changePointSegment.getWayPointList().isEmpty()) {


                    // the first and last point of a walking segment potencially are
                    // change points.
                    Waypoint startPoint = changePointSegment.getWayPointList().get(0);
                    Waypoint endPoint = changePointSegment.getWayPointList().get(changePointSegment.getWayPointList().size() - 1);
                    List<Waypoint> list = Arrays.asList(startPoint, endPoint);

                    for (Waypoint changePoint : list) {
                        TrackSegment track = new TrackSegment();

                        for (Waypoint waypoint : trackSegment.getWayPointList()) {
                            track.add(waypoint);
                            if (waypoint.equals(changePoint)) {
                                break;
                            }
                        }

                        trackSegment.getWayPointList().removeAll(track.getWayPointList());
                        if (!track.getWayPointList().isEmpty()) {
                            partitionedTrackSegments.add(track);
                        }
                    }
                }
            }

        }
        return partitionedTrackSegments;
    }

    private List<ChangePointSegment> makeCertainParition(List<ChangePointSegment> partition) {
        List<ChangePointSegment> certainPartition = new LinkedList<ChangePointSegment>();

        LoopOne:
        for (ChangePointSegment segment : partition) {
            double length = segment.getLength();
            if (length > certainSegmentLengthThreshold) {
                segment.setType(SegmentType.CERTAIN);
                certainPartition.add(segment);
            } else {
                segment.setType(SegmentType.UNCERTAIN);
                List<ChangePointSegment> temp = new ArrayList<ChangePointSegment>(uncertainSegCountThreshold);

                // check whether the result list has the minimum count of elements
                if (certainPartition.size() >= uncertainSegCountThreshold) {

                    // check whether the last <code>uncertainSegCountThreshold</code> segments are of
                    // tyoe UNCERTAIN
                    for (int i = certainPartition.size() - 1 - uncertainSegCountThreshold;
                            i >= 0 && i < certainPartition.size();
                            i++) {
                        ChangePointSegment cps = certainPartition.get(i);
                        if (SegmentType.UNCERTAIN != cps.getType()) {
                            // at least one is not of type UNCERTAIN
                            // add the current segment to the result list and
                            // continue the outer loop
                            certainPartition.add(segment);
                            continue LoopOne;
                        }
                        // collect the last <code>uncertainSegCountThreshold</code> segments
                        temp.add(cps);
                    }

                    // check whether there are enough element in the temp list.
                    if (!temp.isEmpty() && temp.size() == uncertainSegCountThreshold) {

                        ChangePointSegment cps = temp.iterator().next();

                        // merge the uncertainSegCountThreashold + 1 segments
                        // with the first segment and remove them from the
                        // result list.
                        for (int i = 1; i < temp.size(); i++) {
                            cps.addAll(temp.get(i).getWayPointList());
                            certainPartition.remove(temp.get(i));
                        }

                        cps.setTransportMode(Type.NON_WALKING);
                    }

                } else {
                    certainPartition.add(segment);
                }
            }
        }

        return certainPartition;
    }

    private boolean isSegmentValid(ChangePointSegment segment) {
        double segmentLength = segment.getLength();
        long timeDifference = segment.getTimeDifference();
        return segmentLength > minimalDistanceBound && timeDifference > minimalTimeDifference;
    }

    private List<ChangePointSegment> partionWaypoints(List<ChangePointWaypoint> waypointList) {
        List<ChangePointSegment> partition = new ArrayList<ChangePointSegment>(100);

        ChangePointSegment lastSegment = null;
        ChangePointSegment segment = new ChangePointSegment();
        ChangePointWaypoint lastWaypoint = null;
        for (ChangePointWaypoint waypoint : waypointList) {

            if (lastWaypoint != null) {
                // check whether the last and current way point have are
                // of the same type (i.e. walking or non-walking
                if (lastWaypoint.getTransportMode() != waypoint.getTransportMode()) {
                    // they have different types

                    // check whether the current segment satisfies the
                    // necessary creteria to be a stand along segment or
                    // it has to be merged with the previous segment.
                    if (!isSegmentValid(segment)) {
                        // the segment does not satisfy the constrains
                        if (lastSegment != null) {
                            // segment has a previous segment to be merged with.
                            lastSegment.getWayPointList().addAll(segment.getWayPointList());
                            // lastSegment will be not changed
                        } else {
                            // there is no previous segment, because it the
                            // first segment in the partion
                            partition.add(segment);
                            lastSegment = segment;
                        }

                    } else {
                        // the current segment satisfies the constrain and will be
                        // added to the partition as a stand alone segment.
                        partition.add(segment);
                        lastSegment = segment;
                    }
                    // create a new segment
                    segment = new ChangePointSegment();
                } else {
                    // both point are of the same type and the current point
                    // will be added to the current segment.
                    segment.add(waypoint);
                    segment.setTransportMode(waypoint.getTransportMode());
                }
            }

            lastWaypoint = waypoint;
        }

        // check whether the current segment is already in the partition
        // if not add to partition.
        if (!partition.contains(segment)) {
            if (!isSegmentValid(segment)) {
                if (lastSegment != null) {
                    lastSegment.getWayPointList().addAll(segment.getWayPointList());
                } else {
                    partition.add(segment);
                }
            } else {
                partition.add(segment);
            }
        }
        return partition;
    }

    private List<ChangePointWaypoint> transformTo(TrackSegment trackSegment) {
        List<ChangePointWaypoint> waypoints = new LinkedList<ChangePointWaypoint>();
        ChangePointWaypoint lastWaypoint = null;
        for (Waypoint waypoint : trackSegment.getWayPointList()) {
            ChangePointWaypoint changePointWaypoint = new ChangePointWaypoint(waypoint);
            if (lastWaypoint != null) {
                if (changePointWaypoint.getTimestamp() != null && lastWaypoint.getTimestamp() != null) {
                    long timeDiff = (changePointWaypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000;

                    if (timeDiff != 0) {
                        double speedDiff = GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), changePointWaypoint.getLat(), changePointWaypoint.getLon()) / timeDiff;
                        // acceleration in meters / sec^2
                        double acceleration = speedDiff / timeDiff;

                        if (acceleration < looseUpperBoundsAcceleration
                                && lastWaypoint.getSpeed() < looseUpperBoundsVelocity) {
                            changePointWaypoint.setTransportMode(Type.WALKING);
                        } else {
                            changePointWaypoint.setTransportMode(Type.NON_WALKING);
                        }

                        waypoints.add(changePointWaypoint);
                    }
                }
            }
            lastWaypoint = changePointWaypoint;
        }
        return waypoints;
    }

    @NbBundle.Messages("CLT_ChangePointSequencizerFilter_Name=Change Point Sequencizer")
    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_ChangePointSequencizerFilter_Name();
    }

    @NbBundle.Messages("CLT_ChangePointSequencizerFilter_Description=Description")
    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_ChangePointSequencizerFilter_Description();
    }

    @Override
    public void setInput(List<TrackSegment> input) {
        this.gpsTracks = input;
    }

    @Override
    public List<TrackSegment> getResult() {
        List<TrackSegment> arrayList = this.result;
        this.result = null;
        this.gpsTracks = null;
        return arrayList;
    }

    @NbBundle.Messages({
        "CLT_ChangePointSegmentationFilter_Property_Scope_Name=Scope",
        "CLT_ChangePointSegmentationFilter_Property_Scope_Description=Excecution Scope in which Phase of the Detector this filter should be applied.",
        "CLT_ChangePointSegmentationFilter_Property_VelocityBound_Name=Velocity Bound",
        "CLT_ChangePointSegmentationFilter_Property_VelocityBound_Description=GPS tracks will be segmented by change point.",
        "CLT_ChangePointSegmentationFilter_Property_AccelerationBound_Name=Acceleration Bound",
        "CLT_ChangePointSegmentationFilter_Property_AccelerationBound_Description=Specifies a loose upper bound for the acceleration value of a point to declare point as (non) wakling",
        "CLT_ChangePointSegmentationFilter_Property_CertainLengthThreshold_Name=Certain Length Threshold",
        "CLT_ChangePointSegmentationFilter_Property_CertainLengthThreshold_Description=Specifies the length that each segment must exceed to be declared certain.",
        "CLT_ChangePointSegmentationFilter_Property_UncertainCountThreshold_Name=Unchertain Count Threshold",
        "CLT_ChangePointSegmentationFilter_Property_UncertainCountThreshold_Description=Specifies how many successive uncertain segments needs to be to declare uncertain segments as one non-wakling segment",
        "CLT_ChangePointSegmentationFilter_Property_ChangePointMinimalDistanceBound_Name=Minimal Distance Bound",
        "CLT_ChangePointSegmentationFilter_Property_ChangePointMinimalDistanceBound_Description=Specifies the minimal length of an segment with consecutive (non) walking points must exceed if not it will be merged with the backward segment.",
        "CLT_ChangePointSegmentationFilter_Property_ChangePointMinimalTimeDifference_Name=Minimal Time Difference",
        "CLT_ChangePointSegmentationFilter_Property_ChangePointMinimalTimeDifference_Description=Specifies the duration time of an segment with consecutive (non) walking points must exceed"
    })
    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(ChangePointSegmentationFilterProcess.class.getName());
        descriptor.setName(Bundle.CLT_ChangePointSequencizerFilter_Name());
        descriptor.setDescription(Bundle.CLT_ChangePointSequencizerFilter_Description());

        Property property = new Property();
        property.setId(PROP_NAME_FILTER_SCOPE);
        property.setJavaType(InferenceMode.class.getName());
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_Scope_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_Scope_Description());
        property.setValue(InferenceMode.INFERENCE_MODE.toString());
        descriptor.getProperties().getPropertyList().add(property);

        // <!-- velocity value in meters/sec. -->
        property = new Property();
        property.setId(PROP_NAME_LOOSE_UPPER_VELOCITY_BOUND);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_VelocityBound_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_VelocityBound_Description());
        property.setValue("1.8");
        descriptor.getProperties().getPropertyList().add(property);

        //<!-- acceleration in meters/sec^2 -->
        property = new Property();
        property.setId(PROP_NAME_LOOSE_UPPER_ACCELERATION_BOUND);
        property.setJavaType(Double.class.getName());
        property.setValue("0.6");
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_AccelerationBound_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_AccelerationBound_Description());
        descriptor.getProperties().getPropertyList().add(property);

        //<!-- length value in meters -->
        property = new Property();
        property.setId(PROP_NAME_CERTAIN_SEGMENT_LENGTH_THRESHOLD);
        property.setJavaType(Double.class.getName());
        property.setValue("200");
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_CertainLengthThreshold_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_CertainLengthThreshold_Description());
        descriptor.getProperties().getPropertyList().add(property);

        property = new Property();
        property.setId(PROP_NAME_UNCERTAIN_SEGMENT_COUNT_THRESHOLD);
        property.setJavaType(Integer.class.getName());
        property.setValue("2");
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_UncertainCountThreshold_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_UncertainCountThreshold_Description());
        descriptor.getProperties().getPropertyList().add(property);

        // <!--  value in meters -->
        property = new Property();
        property.setId(PROP_NAME_MINIMAL_DISTANCE_BOUND);
        property.setJavaType(Double.class.getName());
        property.setValue("20");
        property.setName(Bundle.CLT_ChangePointSegmentationFilter_Property_ChangePointMinimalDistanceBound_Name());
        property.setDescription(Bundle.CLT_ChangePointSegmentationFilter_Property_ChangePointMinimalDistanceBound_Description());
        descriptor.getProperties().getPropertyList().add(property);

        // <!-- time in seconds -->
        property = new Property();
        property.setId(PROP_NAME_MINIMAL_TIME_DIFFERENCE);
        property.setJavaType(Long.class.getName());
        property.setValue("10");
        property.setName(PROP_NAME_PROCESS_STATE);
        property.setDescription(PROP_NAME_PROCESS_STATE);
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }

    private static class ChangePointSegment extends TrackSegment {

        private SegmentType type = null;
        private Type transportMode = null;

        public Type getTransportMode() {
            return transportMode;
        }

        public void setTransportMode(Type transportMode) {
            this.transportMode = transportMode;
        }

        public SegmentType getType() {
            return type;
        }

        public void setType(SegmentType type) {
            this.type = type;
        }

        public long getTimeDifference() {
            long timeDiff = 0;
            if (getWayPointList().size() > 1) {
                Waypoint endPoint = getWayPointList().get(getWayPointList().size() - 1);
                Waypoint startPoint = getWayPointList().get(0);
                if (endPoint.getTimestamp() != null && startPoint.getTimestamp() != null) {
                    timeDiff = (endPoint.getTimestamp().getTime() - startPoint.getTimestamp().getTime()) / 1000;
                }
            }
            return timeDiff;
        }

        public double getLength() {
            double length = 0;
            Waypoint lastPoint = null;
            for (int i = 0; i < getWayPointList().size(); i++) {
                Waypoint currentPoint = getWayPointList().get(i);
                if (lastPoint != null) {
                    length += GPSCalc.getDistVincentyFast(
                            lastPoint.getLat(),
                            lastPoint.getLon(),
                            currentPoint.getLat(),
                            currentPoint.getLon());
                }
                lastPoint = currentPoint;
            }
            return length;
        }
    }

    private static class ChangePointWaypoint extends GpxWayPoint {

        private Type type = null;
        private final Waypoint original;

        private ChangePointWaypoint(Waypoint original) {
            super(createPropertyMap(original));
            this.original = original;
        }

        public Waypoint getOriginal() {
            return original;
        }

        public Type getTransportMode() {
            return type;
        }

        public void setTransportMode(Type type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            return original.hashCode();
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            return original.equals(obj);
        }

        private static Map<String, String> createPropertyMap(Waypoint waypoint) {
            HashMap<String, String> map = new HashMap<String, String>();
            for (String propertyName : waypoint.getPropertyList()) {
                map.put(propertyName, waypoint.getPropertyValue(propertyName));
            }
            return map;
        }
    }

    private enum SegmentType {

        CERTAIN, UNCERTAIN;
    }

    private enum Type {

        WALKING, NON_WALKING;
    }
}
