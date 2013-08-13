/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.aggregation.strategy;

import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.maps.project.aggregator.factories.nodes.properties.ClassProperty;
import de.fub.maps.project.aggregator.factories.nodes.properties.ClassWrapper;
import de.fub.maps.project.aggregator.pipeline.processes.AggregationProcess;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.AggregationStrategy;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.DescriptorFactory;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.MergeHandler;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.TraceDistance;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.Property;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.aggregator.xml.PropertySet;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.node.property.NodeProperty;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * This is only a wrapper class and its only usage is to annotate this class and
 * keep the agg2graph module clean of the netbeans api.
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "GpxmergeAggregationStrategy_Name=Gpx Aggregation Strategy",
    "GpxmergeAggregationStrategy_Description=No description available",
    "GpxmergeAggregationStrategy_Settings_Name=Gpx Merge Aggregation Settings",
    "GpxmergeAggregationStrategy_Settings_Description=Parameters to configure this aggregations strategy",
    "GpxmergeAggregationStrategy_MaxPathDifference_Name=Max Path Difference",
    "GpxmergeAggregationStrategy_MaxPathDifference_Description=No description available",
    "GpxmergeAggregationStrategy_MaxInitDistance_Name=Max Init Distance",
    "GpxmergeAggregationStrategy_MaxInitDistance_Description=No description available",
    "GpxmergeAggregationStrategy_MergeHandler_Type_Name=MergeHandler Instance Type",
    "GpxmergeAggregationStrategy_MergeHandler_Type_Description=The instance that is responsible for merge the data points",
    "GpxmergeAggregationStrategy_TraceDistance_Type_Name=TraceDistance Instance Type",
    "GpxmergeAggregationStrategy_TraceDistance_Type_Description=The instance which is responsible fo tracing the distance of a segment"
})
@ServiceProvider(service = AggregationStrategy.class)
public class GpxmergeAggregationStrategy extends de.fub.agg2graph.agg.strategy.GpxmergeAggregationStrategy implements AggregationStrategy {

    private static final Logger LOG = Logger.getLogger(GpxmergeAggregationStrategy.class.getName());
    private static final String PROP_NAME_MAX_PATH_DIFFERENCE = "gpxmerge.aggregation.strategy.max.path.difference";
    private static final String PROP_NAME_MAX_INIT_DISTANCE = "gpxmerge.aggregation.strategy.max.init.distance";
    private static final String PROP_NAME_BASE_MERGEHANDLER_TYPE = "gpxmerge.aggregation.strategy.base.mergehandler.type";
    private static final String PROP_NAME_TRACE_DISTANCE_TYPE = "gpx.merge.aggregation.strategy.trace.distance.type";
    private Aggregator aggregator;
    private PropertySection propertySection;
    private GpxAggregationStrategyNode nodeDelegate;

    @Override
    public PropertySection getPropertySection() {
        if (propertySection == null) {
            if (getAggregator() != null) {
                OUTERLOOP:
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (descriptor != null
                            && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        List<PropertySection> sections = descriptor.getProperties().getSections();
                        for (PropertySection section : sections) {
                            if (GpxmergeAggregationStrategy.class.getName().equals(section.getId())) {
                                propertySection = section;
                                break OUTERLOOP;
                            }
                        }
                    }
                }
            }
            if (propertySection == null) {
                propertySection = createDefaultPropertySection();
            }
        }
        return propertySection;
    }

    private PropertySection createDefaultPropertySection() {

        PropertySet propertySet = new PropertySet(
                Bundle.GpxmergeAggregationStrategy_Settings_Name(),
                Bundle.GpxmergeAggregationStrategy_Settings_Description());
        propertySet.setId(GpxmergeAggregationStrategy.class.getName());

        Property property = new Property();
        property.setId(PROP_NAME_MAX_PATH_DIFFERENCE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.GpxmergeAggregationStrategy_MaxPathDifference_Name());
        property.setDescription(Bundle.GpxmergeAggregationStrategy_MaxPathDifference_Description());
        property.setValue(MessageFormat.format("{0}", getMaxPathDifference()).replaceAll("\\,", "."));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_INIT_DISTANCE);
        property.setJavaType(Double.class.getName());
        property.setDescription(Bundle.GpxmergeAggregationStrategy_MaxInitDistance_Description());
        property.setName(Bundle.GpxmergeAggregationStrategy_MaxInitDistance_Name());
        property.setValue(MessageFormat.format("{0}", getMaxInitDistance()).replaceAll("\\,", "."));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_BASE_MERGEHANDLER_TYPE);
        property.setJavaType(String.class.getName());
        property.setDescription(Bundle.GpxmergeAggregationStrategy_MergeHandler_Type_Description());
        property.setName(Bundle.GpxmergeAggregationStrategy_MergeHandler_Type_Name());
        property.setValue(DefaultMergeHandler.class.getName());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_TRACE_DISTANCE_TYPE);
        property.setJavaType(String.class.getName());
        property.setDescription(Bundle.GpxmergeAggregationStrategy_TraceDistance_Type_Description());
        property.setName(Bundle.GpxmergeAggregationStrategy_TraceDistance_Type_Name());
        property.setValue(GpxmergeTraceDistance.class.getName());
        propertySet.getProperties().add(property);

        PropertySection section = new PropertySection(
                Bundle.GpxmergeAggregationStrategy_Settings_Name(),
                Bundle.GpxmergeAggregationStrategy_Settings_Description());
        section.setId(GpxmergeAggregationStrategy.class.getName());
        section.getPropertySet().add(propertySet);

        MergeHandler handler = null;
        try {
            handler = MergeHandler.Factory.getDefault();
            if (handler != null) {
                PropertySet set = handler.getPropertySet();
                if (set != null) {
                    section.getPropertySet().add(set);
                }
            }
        } catch (DescriptorFactory.InstanceNotFountException ex) {
            Exceptions.printStackTrace(ex);
        }

        TraceDistance traceHandler = null;
        try {
            traceHandler = TraceDistance.Factory.find(GpxmergeTraceDistance.class.getName());
            if (traceHandler != null) {
                PropertySet set = traceHandler.getPropertySet();
                if (set != null) {
                    section.getPropertySet().add(set);
                }
            }
        } catch (DescriptorFactory.InstanceNotFountException ex) {
            Exceptions.printStackTrace(ex);
        }

        return section;
    }

    @Override
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        if (aggregator != null) {
            setAggContainer(aggregator.getAggContainer());
        }
        reInit();
    }

    private void reInit() {
        propertySection = null;
        propertySection = getPropertySection();
        if (propertySection != null) {
            for (PropertySet propertySet : propertySection.getPropertySet()) {
                for (Property property : propertySet.getProperties()) {
                    try {
                        if (property.getValue() != null) {
                            if (PROP_NAME_MAX_INIT_DISTANCE.equals(property.getId())) {
                                setMaxInitDistance(Double.parseDouble(property.getValue()));
                                LOG.fine(MessageFormat.format("{0} {1}", PROP_NAME_MAX_INIT_DISTANCE, getMaxInitDistance()));
                            } else if (PROP_NAME_MAX_PATH_DIFFERENCE.equals(property.getId())) {
                                setMaxPathDifference(Double.parseDouble(property.getValue()));
                                LOG.fine(MessageFormat.format("{0} {1}", PROP_NAME_MAX_PATH_DIFFERENCE, getMaxPathDifference()));
                            } else if (PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                                MergeHandler handler = MergeHandler.Factory.find(property.getValue(), getAggregator());
                                if (handler == null) {
                                    handler = new DefaultMergeHandler();
                                    handler.setAggregator(getAggregator());
                                }
                                baseMergeHandler = handler;
                                LOG.log(Level.FINE, PROP_NAME_BASE_MERGEHANDLER_TYPE + " {0}", baseMergeHandler.getClass());
                            } else if (PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
                                TraceDistance traceDistanceHandler = TraceDistance.Factory.find(property.getValue(), getAggregator());
                                if (traceDistanceHandler == null) {
                                    traceDistanceHandler = new GpxmergeTraceDistance();
                                    traceDistanceHandler.setAggregator(aggregator);
                                }
                                traceDistance = traceDistanceHandler;
                                LOG.fine(MessageFormat.format("{0} {1}", PROP_NAME_TRACE_DISTANCE_TYPE, traceDistance.getClass()));
                            }
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    @Override
    public Aggregator getAggregator() {
        return this.aggregator;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new GpxAggregationStrategyNode(GpxmergeAggregationStrategy.this);
        }
        return nodeDelegate;
    }

    private static class GpxAggregationStrategyNode extends AbstractNode {

        private final GpxmergeAggregationStrategy strategy;

        public GpxAggregationStrategyNode(GpxmergeAggregationStrategy strategy) {
            super(Children.LEAF);
            this.strategy = strategy;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            PropertySet[] sets = sheet.toArray();
            for (PropertySet set : sets) {
                sheet.remove(set.getName());
            }

            if (this.strategy != null) {
                final PropertySection propertySection = this.strategy.getPropertySection();
                if (propertySection != null) {

                    for (de.fub.maps.project.aggregator.xml.PropertySet propertySet : propertySection.getPropertySet()) {
                        if (propertySet.getId().equals(GpxmergeAggregationStrategy.class.getName())) {
                            Sheet.Set set = Sheet.createPropertiesSet();
                            set.setName(propertySet.getName());
                            set.setDisplayName(propertySet.getName());
                            set.setShortDescription(propertySet.getDescription());
                            sheet.put(set);

                            for (final de.fub.maps.project.aggregator.xml.Property property : propertySet.getProperties()) {
                                if (PROP_NAME_MAX_PATH_DIFFERENCE.equals(property.getId())) {
                                    set.put(new NodeProperty(property));
                                } else if (PROP_NAME_MAX_INIT_DISTANCE.equals(property.getId())) {
                                    set.put(new NodeProperty(property));
                                } else if (PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                                    ClassProperty classProperty = new ClassProperty(property.getId(), property.getName(), property.getDescription(), MergeHandler.class) {
                                        private ClassWrapper wrapper = null; //strategy.getBaseMergeHandler() != null ? new ClassWrapper(strategy.getBaseMergeHandler().getClass()) : null;

                                        @Override
                                        public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                                            return wrapper;
                                        }

                                        @Override
                                        public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                            if (strategy.getAggregator().getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                                                if (val == null) {
                                                    throw new IllegalArgumentException("null is not a valid argument.");
                                                } else if (!val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                                                    wrapper = val;
                                                    property.setValue(val.getQualifiedName());
                                                }
                                            }
                                        }
                                    };
                                    set.put(classProperty);
                                } else if (PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
                                    ClassProperty classProperty = new ClassProperty(property.getId(), property.getName(), property.getDescription(), TraceDistance.class) {
                                        private ClassWrapper wrapper = strategy.getTraceDist() != null
                                                ? new ClassWrapper(strategy.getTraceDist().getClass())
                                                : null;

                                        @Override
                                        public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                                            return wrapper;
                                        }

                                        @Override
                                        public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                            if (strategy.getAggregator().getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                                                if (val == null) {
                                                    throw new IllegalArgumentException("null is not a valid argument.");
                                                } else if (!val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                                                    List<de.fub.maps.project.aggregator.xml.PropertySet> propertySets = propertySection.getPropertySet();
                                                    for (de.fub.maps.project.aggregator.xml.PropertySet p : propertySets) {
                                                        if (p.getId().equals(val.getQualifiedName())) {
                                                            propertySets.remove(p);
                                                            break;
                                                        }
                                                    }


                                                    wrapper = val;
                                                    property.setValue(val.getQualifiedName());
                                                }
                                            }
                                        }
                                    };
                                    set.put(classProperty);
                                }
                            }
                        }
                    }


//                    IMergeHandler baseMergeHandler = strategy.getBaseMergeHandler();
//                    if (baseMergeHandler instanceof MergeHandler) {
//                        MergeHandler mergeHandler = (MergeHandler) baseMergeHandler;
//                        PropertySet[] propertySets = mergeHandler.getNodeDelegate().getPropertySets();
//
//                        for (PropertySet propertySet : propertySets) {
//                            Sheet.Set set = Sheet.createPropertiesSet();
//                            set.setName(propertySet.getName());
//                            set.setDisplayName(propertySet.getDisplayName());
//                            set.setShortDescription(propertySet.getShortDescription());
//                            sheet.put(set);
//
//                            for (Property<?> nodeProperty : propertySet.getProperties()) {
//                                set.put(nodeProperty);
//                            }
//                        }
//                    }
                    ITraceDistance traceDist = strategy.getTraceDist();
                    if (traceDist instanceof TraceDistance) {
                        TraceDistance traceDistance = (TraceDistance) traceDist;
                        PropertySet[] propertySets = traceDistance.getNodeDelegate().getPropertySets();

                        for (PropertySet propertySet : propertySets) {
                            Sheet.Set set = Sheet.createPropertiesSet();
                            set.setName(propertySet.getName());
                            set.setDisplayName(propertySet.getDisplayName());
                            set.setShortDescription(propertySet.getShortDescription());
                            sheet.put(set);

                            for (Property<?> property : propertySet.getProperties()) {
                                set.put(property);
                            }
                        }
                    }
                }
            }

            return sheet;
        }
    }
}
