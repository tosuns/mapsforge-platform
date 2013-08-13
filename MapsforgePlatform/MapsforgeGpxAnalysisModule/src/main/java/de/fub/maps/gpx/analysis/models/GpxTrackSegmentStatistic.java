/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.gpx.analysis.models;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.features.AvgAccelerationFeature;
import de.fub.agg2graph.gpseval.features.AvgVelocityFeature;
import de.fub.agg2graph.gpseval.features.ErrorRateFeature;
import de.fub.agg2graph.gpseval.features.HeadingChangeRateFeature;
import de.fub.agg2graph.gpseval.features.MaxNAccelerationFeature;
import de.fub.agg2graph.gpseval.features.MaxNVelocityFeature;
import de.fub.agg2graph.gpseval.features.MeanVelocityFeature;
import de.fub.agg2graph.gpseval.features.TrackLengthFeature;
import de.fub.agg2graph.gpseval.features.VarianceOfVelocityFeature;
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
    private Double max2Velocity;
    private Double max3Velocity;
    private Double varianceOfVelocity;
    private Double averageAcceleration;
    private Double maxAcceleration;
    private Double max2Acceleration;
    private Double max3Acceleration;
    private Double errorRate;
    private Double headingChangeRate;
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

    public Double getMax2Velocity() {
        if (max2Velocity == null) {
            MaxNVelocityFeature feature = new MaxNVelocityFeature(2);
            for (Waypoint waypoint : this.list) {
                feature.addWaypoint(waypoint);
            }
            max2Velocity = feature.getResult();
        }
        return max2Velocity;
    }

    public Double getMax3Velocity() {
        if (max3Velocity == null) {
            MaxNVelocityFeature feature = new MaxNVelocityFeature(3);
            for (Waypoint waypoint : this.list) {
                feature.addWaypoint(waypoint);
            }
            max3Velocity = feature.getResult();
        }
        return max3Velocity;
    }

    public Double getVarianceOfVelocity() {
        if (varianceOfVelocity == null) {
            VarianceOfVelocityFeature varianceOfVelocityFeature = new VarianceOfVelocityFeature();
            for (Waypoint waypoint : this.list) {
                varianceOfVelocityFeature.addWaypoint(waypoint);
            }
            varianceOfVelocity = varianceOfVelocityFeature.getResult();
        }
        return varianceOfVelocity;
    }

    public Double getMax2Acceleration() {
        if (max2Acceleration == null) {
            MaxNAccelerationFeature feature = new MaxNAccelerationFeature(2);
            for (Waypoint waypoint : this.list) {
                feature.addWaypoint(waypoint);
            }
            max2Acceleration = feature.getResult();
        }
        return max2Acceleration;
    }

    public Double getMax3Acceleration() {
        if (max3Acceleration == null) {
            MaxNAccelerationFeature feature = new MaxNAccelerationFeature(3);
            for (Waypoint waypoint : this.list) {
                feature.addWaypoint(waypoint);
            }
            max3Acceleration = feature.getResult();
        }
        return max3Acceleration;
    }

    public Double getErrorRate() {
        if (errorRate == null) {
            ErrorRateFeature feature = new ErrorRateFeature();
            for (Waypoint waypoint : this.list) {
                feature.addWaypoint(waypoint);
            }
            errorRate = feature.getResult();
        }
        return errorRate;
    }

    public Double getHeadingChangeRate() {
        if (headingChangeRate == null) {
            HeadingChangeRateFeature feature = new HeadingChangeRateFeature();
            for (Waypoint waypoint : this.list) {
                feature.addWaypoint(waypoint);
            }
            headingChangeRate = feature.getResult();
        }
        return headingChangeRate;
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
            "maxAcceleration_Description=The maximum acceleration of this track segment in meter per square second",
            "max2Acceleration_Name=2nd Maximum Acceleration",
            "max2Acceleration_Description=The 2nd maximum acceleration of this track segment in meter per square second",
            "max3Acceleration_Name=3rd Maximum Acceleration",
            "max3Acceleration_Description=The 3rd maximum acceleration of this track segment in meter per sequare seconds",
            "avgVelocity_Name=Average Velocity",
            "avgVelocity_Description=The average velocity of this track segment in Km/h",
            "maxVelocity_Name=Maximum Velocity",
            "maxVelocity_Description=The maximum velocity of this track segment in Km/h",
            "max2Velocity_Name=2nd Maximum Velocity",
            "max2Velocity_Description=The 2nd maximum velocity of this track segment in Km/h",
            "max3Velocity_Name=3rd Maximum Velocity",
            "max3Velocity_Description=The 3rd maximum velocity of this track segment in Km/h",
            "meanVelocity_Name=Mean Velocity",
            "meanVelocity_Description=The mean velocity of this track segment in Km/h",
            "totalLength_Name=Segment Length",
            "totalLength_Description=The total track segment length in kilometer",
            "headingChangeRate_Name=Heading Change Rate",
            "headingChangeRate_Description=The frequency the heading exceeds a predefined default angle in degree",
            "errorRate_Name=Error Rate",
            "errorRate_Description=determines the number of error measurement that occurs within this track segment.",
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

            property = new PropertySupport.ReadOnly<String>("max2Accelection", String.class, Bundle.max2Acceleration_Name(), Bundle.max2Acceleration_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} m/s\u00b2", MESSAGE_PATTERN), statistics.getMax2Acceleration());
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("max3Accelection", String.class, Bundle.max3Acceleration_Name(), Bundle.max3Acceleration_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} m/s\u00b2", MESSAGE_PATTERN), statistics.getMax3Acceleration());
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

            property = new PropertySupport.ReadOnly<String>("max2Velocity", String.class, Bundle.max2Velocity_Name(), Bundle.max2Velocity_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} Km/h", MESSAGE_PATTERN), statistics.getMax2Velocity() * KILOMETER_PER_HOUR_FACTOR);
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("max3Velocity", String.class, Bundle.max3Velocity_Name(), Bundle.max3Velocity_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format(MessageFormat.format("{0} Km/h", MESSAGE_PATTERN), statistics.getMax3Velocity() * KILOMETER_PER_HOUR_FACTOR);
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

            property = new PropertySupport.ReadOnly<String>("headingChangeRate", String.class, Bundle.headingChangeRate_Name(), Bundle.headingChangeRate_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format("{0}", statistics.getHeadingChangeRate());
                    }
                    return value;
                }
            };
            property.setValue(SUPPRESS_CUSTOM_EDITOR_PROPERTY, Boolean.TRUE);
            set.put(property);

            property = new PropertySupport.ReadOnly<String>("errorRate", String.class, Bundle.errorRate_Name(), Bundle.errorRate_Description()) {
                private String value;

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (value == null) {
                        value = MessageFormat.format("{0}", statistics.getErrorRate());
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
