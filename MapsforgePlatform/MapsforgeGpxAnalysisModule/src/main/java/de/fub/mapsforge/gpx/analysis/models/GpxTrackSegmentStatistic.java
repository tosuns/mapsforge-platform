/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.gpx.analysis.models;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.AvgAccelerationFeature;
import de.fub.agg2graph.gpseval.features.AvgVelocityFeature;
import de.fub.agg2graph.gpseval.features.MaxNAccelerationFeature;
import de.fub.agg2graph.gpseval.features.MaxNVelocityFeature;
import de.fub.agg2graph.gpseval.features.MeanVelocityFeature;
import de.fub.agg2graph.gpseval.features.TrackLengthFeature;
import de.fub.gpxmodule.xml.Trkseg;
import de.fub.gpxmodule.xml.Wpt;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class GpxTrackSegmentStatistic implements Statistic {

    private Double totalLength;
    private Double averageVelocity;
    private Double meanVelocity;
    private Double maxVelocity;
    private Double averageAcceleration;
    private Double maxAcceleration;
    private final ArrayList<Waypoint> list;
    private StatisticNode node;
    private Trkseg gpxTrackSegment = null;

    public GpxTrackSegmentStatistic(Trkseg trkseg) {
        this.gpxTrackSegment = trkseg;
        this.list = new ArrayList<Waypoint>(trkseg.getTrkpt().size());
        for (Wpt wpt : trkseg.getTrkpt()) {
            this.list.add(new CustomWaypoint(wpt));
        }
    }

    public Trkseg getGpxTrackSegment() {
        return gpxTrackSegment;
    }

    @Override
    public Node getNodeDelegate() {
        if (node == null) {
            node = new StatisticNode(this);
        }
        return node;
    }

    public List<Waypoint> getTrackSegment() {
        return Collections.unmodifiableList(list);
    }

    public double getTotalLength() {
        if (totalLength == null) {
            TrackLengthFeature trackLengthFeature = new TrackLengthFeature();
            for (Waypoint waypoint : this.list) {
                trackLengthFeature.addWaypoint(waypoint);
            }
            totalLength = trackLengthFeature.getResult();
        }
        return totalLength;
    }

    public int getTotalPointCount() {
        return this.list.size();
    }

    public double getAverageVelocity() {
        if (averageVelocity == null) {
            AvgVelocityFeature avgSpeedFeature = new AvgVelocityFeature();
            for (Waypoint waypoint : this.list) {
                avgSpeedFeature.addWaypoint(waypoint);
            }
            averageVelocity = avgSpeedFeature.getResult();
        }
        return averageVelocity;
    }

    public double getMeanVelocity() {
        if (meanVelocity == null) {
            MeanVelocityFeature meanVelocityFeature = new MeanVelocityFeature();
            for (Waypoint waypoint : this.list) {
                meanVelocityFeature.addWaypoint(waypoint);
            }
            meanVelocity = meanVelocityFeature.getResult();
        }
        return meanVelocity;
    }

    public double getMaxVelocity() {
        if (maxVelocity == null) {
            MaxNVelocityFeature maxVelocityFeature = new MaxNVelocityFeature(1);
            for (Waypoint waypoint : this.list) {
                maxVelocityFeature.addWaypoint(waypoint);
            }
            maxVelocity = maxVelocityFeature.getResult();
        }
        return maxVelocity;
    }

    public double getAverageAcceleration() {
        if (averageAcceleration == null) {
            AvgAccelerationFeature avgAccelerationFeature = new AvgAccelerationFeature();
            for (Waypoint waypoint : this.list) {
                avgAccelerationFeature.addWaypoint(waypoint);
            }
            averageAcceleration = avgAccelerationFeature.getResult();
        }
        return averageAcceleration;
    }

    public double getMaxAcceleration() {
        if (maxAcceleration == null) {
            MaxNAccelerationFeature maxAccelerationFeature = new MaxNAccelerationFeature(1);
            for (Waypoint waypoint : this.list) {
                maxAccelerationFeature.addWaypoint(waypoint);
            }
            maxAcceleration = maxAccelerationFeature.getResult();
        }
        return maxAcceleration;
    }

    private static class CustomWaypoint extends Waypoint {

        private final Wpt gpxWpt;

        public CustomWaypoint(Wpt gpxWpt) {
            this.gpxWpt = gpxWpt;
            init();
        }

        private void init() {
            this.lon = gpxWpt.getLon() != null ? gpxWpt.getLon().doubleValue() : 0;
            this.lat = gpxWpt.getLat() != null ? gpxWpt.getLat().doubleValue() : 9;
            this.timestamp = gpxWpt.getTime();
        }
    }

    private static class StatisticNode extends AbstractNode {

        private final GpxTrackSegmentStatistic statistics;
        private static final String MESSAGE_PATTERN = "{0, number, 0.00}";
//        private static final MessageFormat MESSAGE_FORMAT = new MessageFormat(MESSAGE_PATTERN, Locale.ENGLISH);
        private static final double KILOMETER_PER_HOUR_FACTOR = 3.6;
        private static final String SUPPRESS_CUSTOM_EDITOR_PROPERTY = "suppressCustomEditor";

        public StatisticNode(GpxTrackSegmentStatistic statistics) {
            super(Children.LEAF);
            this.statistics = statistics;
        }

        @NbBundle.Messages({
            "avgAcceleration_Name=Average Acceleration",
            "avgAcceleration_Description=The average acceleration of this track segment in meter per square second",
            "maxAcceleration_Name=Maximum Acceleration",
            "maxAcceleration_Description=The maximum acceleration of this track segment in meter per square seconf",
            "avgVelocity_Name=Average Velocity",
            "avgVelocity_Description=The average velocity of this track segment in Km/h",
            "maxVelocity_Name=Maximum Velocity",
            "maxVelocity_Description=The maximum velocity of this track segment in Km/h",
            "meanVelocity_Name=Mean Velocity",
            "meanVelocity_Description=The mean velocity of this track segment in Km/h",
            "totalLength_Name=Segment Length",
            "totalLength_Description=The total track segment length in kilometer",
            "waypointCount_Name=Waypoint Count",
            "waypointCount_Description=The number of gps coordinates which this track segment contains."
        })
        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = Sheet.createPropertiesSet();
            sheet.put(set);

            Node.Property<?> property = new PropertySupport.ReadOnly<String>("avgAcceleration", String.class, Bundle.avgAcceleration_Name(), Bundle.avgAcceleration_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} m/s\u00b2", MESSAGE_PATTERN), statistics.getAverageAcceleration());
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("avgVelocity", String.class, Bundle.avgVelocity_Name(), Bundle.avgVelocity_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} Km/h", MESSAGE_PATTERN), statistics.getAverageVelocity() * KILOMETER_PER_HOUR_FACTOR);
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("maxAcceleration", String.class, Bundle.maxAcceleration_Name(), Bundle.maxAcceleration_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} m/s\u00b2", MESSAGE_PATTERN), statistics.getMaxAcceleration());
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("maxVelocity", String.class, Bundle.maxVelocity_Name(), Bundle.maxVelocity_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} Km/h", MESSAGE_PATTERN), statistics.getMaxVelocity() * KILOMETER_PER_HOUR_FACTOR);
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("meanVelocity", String.class, Bundle.meanVelocity_Name(), Bundle.meanVelocity_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} Km/h", MESSAGE_PATTERN), statistics.getMeanVelocity() * KILOMETER_PER_HOUR_FACTOR);
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("totalLength", String.class, Bundle.totalLength_Name(), Bundle.totalLength_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} Km", MESSAGE_PATTERN), statistics.getTotalLength() / 1000);
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<Integer>("pointCount", Integer.class, Bundle.waypointCount_Name(), Bundle.waypointCount_Description()) {
                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return statistics.getTotalPointCount();
                }
            };
            set.put(property);

            return sheet;
        }
    }
}
