/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.aggregation.strategy;

import de.fub.maps.project.aggregator.pipeline.processes.AggregationProcess;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.TraceDistance;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.Property;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.aggregator.xml.PropertySet;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.node.property.NodeProperty;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "DefaultTraceDistance_Name=Default Trace Distance",
    "DefaultTraceDistance_Description=No Description available",
    "DefaultTraceDistance_AggReflectionFactor_Name=Aggregation Reflection Factor",
    "DefaultTraceDistance_AggReflectionFactor_Description=No description available",
    "DefaultTraceDistance_MaxOutliners_Name=Max Outliners",
    "DefaultTraceDistance_MaxOutliners_Description=No description available",
    "DefaultTraceDistance_MaxDistance_Name=Max Distance",
    "DefaultTraceDistance_MaxDistance_Description=No description available",
    "DefaultTraceDistance_MaxLookahead_Name=Max Lookahead",
    "DefaultTraceDistance_MaxLookahead_Description=No description available",
    "DefaultTraceDistance_MaxPathDifference_Name=Max Path Difference",
    "DefaultTraceDistance_MaxPathDifference_Description=No description available",
    "DefaultTraceDistance_MinLengthFirstSegment_Name=Min Length First Segment",
    "DefaultTraceDistance_MinLengthFirstSegment_Description=No description available",
    "DefaultTraceDistance_MaxAngle_Name=Max Angle",
    "DefaultTraceDistance_MaxAngle_Descriptionn=No description available",
    "DefaultTraceDistance_Settings_PropertySet_Name=Default Trace Distance Settings",
    "DefaultTraceDistance_Settings_PropertySet_Description=Parameters to configure this TraceDistance"
})
@ServiceProvider(service = TraceDistance.class)
public class DefaultTraceDistance extends de.fub.agg2graph.agg.strategy.DefaultTraceDistance implements TraceDistance {

    private static final Logger LOG = Logger.getLogger(DefaultTraceDistance.class.getName());
    private static final String PROP_NAME_AGG_REFLECTIONFACTOR = "default.trace.distance.aggreflectionFactor";
    private static final String PROP_NAME_MAX_OUTLINERS = "default.trace.distance.maxOutliners";
    private static final String PROP_NAME_MAX_DISTANCE = "default.trace.distance.maxDistance";
    private static final String PROP_NAME_MAX_LOOKAHEAD = "default.trace.maxLookahead";
    private static final String PROP_NAME_MAX_PATH_DIFFERENCE = "default.trace.distance.maxPathDifference";
    private static final String PROP_NAME_MIN_LENGTH_FIRST_SEGMENT = "default.trace.distance.maxLengthFirstSegment";
    private static final String PROP_NAME_MAX_ANGLE = "default.trace.distance.maxAngle";
    private Aggregator aggregator;
    private PropertySet propertySet;
    private TraceDistanceNode nodeDelegate;

    @Override
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        reInit();
    }

    private void reInit() {
        propertySet = null;
        propertySet = getPropertySet();
        if (propertySet != null) {

            List<Property> properties = propertySet.getProperties();
            for (Property property : properties) {
                try {
                    if (property.getValue() != null) {
                        if (PROP_NAME_AGG_REFLECTIONFACTOR.equals(property.getId())) {
                            setAggReflectionFactor(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MAX_ANGLE.equals(property.getId())) {
                            setMaxAngle(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MAX_DISTANCE.equals(property.getId())) {
                            setMaxDistance(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MAX_LOOKAHEAD.equals(property.getId())) {
                            setMaxLookahead(Integer.parseInt(property.getValue()));
                        } else if (PROP_NAME_MAX_OUTLINERS.equals(property.getId())) {
                            setMaxOutliners(Integer.parseInt(property.getValue()));
                        } else if (PROP_NAME_MAX_PATH_DIFFERENCE.equals(property.getId())) {
                            setMaxPathDifference(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MIN_LENGTH_FIRST_SEGMENT.equals(property.getId())) {
                            setMinLengthFirstSegment(Integer.parseInt(property.getValue()));
                        }
                    }
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

        }
    }

    @Override
    public Aggregator getAggregator() {
        return this.aggregator;
    }

    @Override
    public PropertySet getPropertySet() {
        if (propertySet == null) {
            if (getAggregator() != null) {
                OUTERLOOP:
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (descriptor != null
                            && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        List<PropertySection> sections = descriptor.getProperties().getSections();
                        for (PropertySection section : sections) {
                            for (PropertySet set : section.getPropertySet()) {
                                if (DefaultTraceDistance.class.getName().equals(set.getId())) {
                                    propertySet = set;
                                    break OUTERLOOP;
                                }
                            }
                        }
                    }
                }
            }
            if (propertySet == null) {
                propertySet = createDefaultPropertySet();
            }
        }
        return propertySet;
    }

    private PropertySet createDefaultPropertySet() {

        PropertySet set = new PropertySet(
                Bundle.DefaultTraceDistance_Settings_PropertySet_Name(),
                Bundle.DefaultTraceDistance_Settings_PropertySet_Description());
        set.setId(DefaultTraceDistance.class.getName());

        Property property = new Property();
        property.setId(PROP_NAME_AGG_REFLECTIONFACTOR);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.DefaultTraceDistance_AggReflectionFactor_Name());
        property.setDescription(Bundle.DefaultTraceDistance_AggReflectionFactor_Description());
        property.setValue(MessageFormat.format("{0}", getAggReflectionFactor()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_ANGLE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.DefaultTraceDistance_MaxAngle_Name());
        property.setDescription(Bundle.DefaultTraceDistance_MaxAngle_Descriptionn());
        property.setValue(MessageFormat.format("{0}", getMaxAngle()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_DISTANCE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.DefaultTraceDistance_MaxDistance_Name());
        property.setDescription(Bundle.DefaultTraceDistance_MaxDistance_Description());
        property.setValue(MessageFormat.format("{0}", getMaxDistance()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_LOOKAHEAD);
        property.setJavaType(Integer.class.getName());
        property.setName(Bundle.DefaultTraceDistance_MaxLookahead_Name());
        property.setDescription(Bundle.DefaultTraceDistance_MaxLookahead_Description());
        property.setValue(MessageFormat.format("{0}", getMaxLookahead()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_OUTLINERS);
        property.setJavaType(Integer.class.getName());
        property.setName(Bundle.DefaultTraceDistance_MaxOutliners_Name());
        property.setDescription(Bundle.DefaultTraceDistance_MaxOutliners_Description());
        property.setValue(MessageFormat.format("{0}", getMaxOutliners()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_PATH_DIFFERENCE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.DefaultTraceDistance_MaxPathDifference_Name());
        property.setDescription(Bundle.DefaultTraceDistance_MaxPathDifference_Description());
        property.setValue(MessageFormat.format("{0}", getMaxPathDifference()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_LENGTH_FIRST_SEGMENT);
        property.setJavaType(Integer.class.getName());
        property.setName(Bundle.DefaultTraceDistance_MinLengthFirstSegment_Name());
        property.setDescription(Bundle.DefaultTraceDistance_MinLengthFirstSegment_Name());
        property.setValue(MessageFormat.format("{0}", getMinLengthFirstSegment()).replaceAll("\\,", "."));
        set.getProperties().add(property);



        return set;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new TraceDistanceNode(DefaultTraceDistance.this);
        }
        return nodeDelegate;
    }

    private static class TraceDistanceNode extends AbstractNode {

        private final DefaultTraceDistance traceDistance;

        public TraceDistanceNode(DefaultTraceDistance traceDistance) {
            super(Children.LEAF);
            this.traceDistance = traceDistance;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            PropertySet[] propertySets = sheet.toArray();

            for (PropertySet set : propertySets) {
                sheet.remove(set.getName());
            }
            if (this.traceDistance != null) {
                de.fub.maps.project.aggregator.xml.PropertySet propertySet = this.traceDistance.getPropertySet();
                if (propertySet != null) {
                    Sheet.Set set = Sheet.createPropertiesSet();
                    set.setName(propertySet.getId());
                    set.setDisplayName(propertySet.getName());
                    set.setShortDescription(propertySet.getDescription());
                    sheet.put(set);

                    for (de.fub.maps.project.aggregator.xml.Property property : propertySet.getProperties()) {
                        set.put(new NodeProperty(property));
                    }

                }
            }
            return sheet;
        }
    }
}
