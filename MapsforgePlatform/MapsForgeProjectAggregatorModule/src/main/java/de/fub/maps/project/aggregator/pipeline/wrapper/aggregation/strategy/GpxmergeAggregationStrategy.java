/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.aggregation.strategy;

import de.fub.agg2graph.agg.IMergeHandler;
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
import java.util.ArrayList;
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
    protected PropertySection propertySection = null;
    protected Aggregator aggregator;
    protected Node nodeDelegate = null;

    public GpxmergeAggregationStrategy() {
    }

    @Override
    public void setTraceDistance(ITraceDistance traceDistance) {
        this.traceDistance = traceDistance;
    }

    @Override
    public void setBaseMergeHandler(IMergeHandler baseMergeHandler) {
        this.baseMergeHandler = baseMergeHandler;
    }

    @Override
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        if (aggregator != null) {
            setAggContainer(aggregator.getAggContainer());
        }
        reInit();
    }

    @Override
    public Aggregator getAggregator() {
        return this.aggregator;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new StrategyNode(GpxmergeAggregationStrategy.this);
        }
        return nodeDelegate;
    }

    @Override
    public PropertySection getPropertySection() {
        if (propertySection == null) {
            if (getAggregator() != null) {
                OUTERLOOP:
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (descriptor != null && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        for (PropertySection section : descriptor.getProperties().getSections()) {
                            if (GpxmergeAggregationStrategy.class.getName().equals(section.getId())) {
                                propertySection = section;
                                break OUTERLOOP;
                            }
                        }
                    }
                }
            }
            if (propertySection == null) {
                propertySection = createDefaultDescriptor();
            }
        }
        return propertySection;
    }

    protected PropertySection createDefaultDescriptor() {

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

    protected void reInit() {
        nodeDelegate = null;
        propertySection = null;
        if (getPropertySection() != null) {
            for (PropertySet propertySet : getPropertySection().getPropertySet()) {
                if (Bundle.GpxmergeAggregationStrategy_Settings_Name().equals(propertySet.getName())) {
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
                                    setBaseMergeHandler(handler);
                                    LOG.log(Level.FINE, PROP_NAME_BASE_MERGEHANDLER_TYPE + " {0}", baseMergeHandler.getClass());
                                } else if (PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
                                    TraceDistance traceDistanceHandler = TraceDistance.Factory.find(property.getValue(), getAggregator());
                                    if (traceDistanceHandler == null) {
                                        traceDistanceHandler = new GpxmergeTraceDistance();
                                        traceDistanceHandler.setAggregator(getAggregator());
                                    }
                                    setTraceDistance(traceDistanceHandler);
                                    LOG.fine(MessageFormat.format("{0} {1}", PROP_NAME_TRACE_DISTANCE_TYPE, traceDistance.getClass()));
                                }
                            }
                        } catch (DescriptorFactory.InstanceNotFountException ex) {
                            LOG.log(Level.SEVERE, ex.getMessage(), ex);
                        } catch (NumberFormatException ex) {
                            LOG.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
    }

    private static class StrategyNode extends AbstractNode {

        private static final Logger LOG = Logger.getLogger(StrategyNode.class.getName());

        private final GpxmergeAggregationStrategy strategy;

        public StrategyNode(GpxmergeAggregationStrategy strategy) {
            super(Children.LEAF);
            this.strategy = strategy;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();

            if (strategy != null) {
                final PropertySection propertySection = strategy.getPropertySection();
                if (propertySection != null) {
                    for (final de.fub.maps.project.aggregator.xml.PropertySet propertySet : propertySection.getPropertySet()) {
                        if (GpxmergeAggregationStrategy.class.getName().equals(propertySet.getId())) {
                            Sheet.Set set = Sheet.createPropertiesSet();
                            sheet.put(set);
                            set.setName(propertySet.getId());
                            set.setDisplayName(propertySet.getName());
                            set.setShortDescription(propertySet.getDescription());
                            List<de.fub.maps.project.aggregator.xml.Property> properties = propertySet.getProperties();
                            for (final de.fub.maps.project.aggregator.xml.Property property : properties) {
                                if (GpxmergeAggregationStrategy.PROP_NAME_MAX_PATH_DIFFERENCE.equals(property.getId())) {
                                    set.put(new NodeProperty(property));
                                } else if (GpxmergeAggregationStrategy.PROP_NAME_MAX_INIT_DISTANCE.equals(property.getId())) {
                                    set.put(new NodeProperty(property));
                                } else if (GpxmergeAggregationStrategy.PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                                    ClassProperty classProperty = new MergeHandlerProperty(propertySection, property);
                                    set.put(classProperty);
                                } else if (GpxmergeAggregationStrategy.PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
                                    ClassProperty classProperty = new TractDistanceProperty(propertySection, property);
                                    set.put(classProperty);
                                }
                            }
                        }
                    }
                    IMergeHandler baseMergeHandler1 = strategy.getBaseMergeHandler();
                    if (baseMergeHandler1 instanceof MergeHandler) {
                        MergeHandler mergeHandler = (MergeHandler) baseMergeHandler1;
                        PropertySet[] propertySets = mergeHandler.getNodeDelegate().getPropertySets();
                        for (Node.PropertySet propertySet : propertySets) {
                            Sheet.Set set = convertToSet(propertySet);
                            sheet.put(set);
                        }
                    }

                    ITraceDistance traceDist = strategy.getTraceDist();
                    if (traceDist instanceof TraceDistance) {
                        TraceDistance traceDistance = (TraceDistance) traceDist;
                        Node.PropertySet[] propertySets = traceDistance.getNodeDelegate().getPropertySets();
                        for (Node.PropertySet propertySet : propertySets) {
                            Sheet.Set set = convertToSet(propertySet);
                            sheet.put(set);
                        }
                    }
                }
            }
            return sheet;
        }

        private Sheet.Set convertToSet(Node.PropertySet propertySet) {
            Sheet.Set set = Sheet.createPropertiesSet();
            set.setName(propertySet.getName());
            set.setDisplayName(propertySet.getDisplayName());
            set.setShortDescription(propertySet.getShortDescription());
            for (Node.Property<?> property : propertySet.getProperties()) {
                set.put(property);
            }
            return set;
        }

        private class TractDistanceProperty extends ClassProperty {

            private final PropertySection propertySection;
            private final de.fub.maps.project.aggregator.xml.Property property;
            private ClassWrapper wrapper;

            public TractDistanceProperty(PropertySection propertySection, de.fub.maps.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), TraceDistance.class);
                this.propertySection = propertySection;
                this.property = property;
                wrapper = strategy.getTraceDist() != null
                        ? new ClassWrapper(strategy.getTraceDist().getClass())
                        : new ClassWrapper(TraceDistance.class);
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            @SuppressWarnings(value = "unchecked")
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (strategy.getAggregator().getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    if (val == null) {
                        throw new IllegalArgumentException("null is not a valid argument.");
                    } else if (wrapper == null || !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                        // only if there is a change proceed.
                        List<de.fub.maps.project.aggregator.xml.PropertySet> propertySets = propertySection.getPropertySet();
                        ClassWrapper oldValue = getValue();
                        wrapper = val;
                        int index = -1;
                        if (oldValue != null) {
                            ArrayList<de.fub.maps.project.aggregator.xml.PropertySet> copyList = new ArrayList<de.fub.maps.project.aggregator.xml.PropertySet>(propertySets);
                            // look for the associated propertySet for the old value
                            for (de.fub.maps.project.aggregator.xml.PropertySet propertySet : copyList) {
                                index++;
                                if (propertySet.getId().equals(oldValue.getQualifiedName())) {
                                    // remove the associated propertySet
                                    LOG.info(MessageFormat.format("{0} removed: {1}", propertySet.getId(), propertySets.remove(propertySet))); //
                                    break;
                                }
                            }
                        }
                        // create the respective TraceInstance and update the PropertySet
                        // in the property Set list
                        try {
                            TraceDistance traceDistance = TraceDistance.Factory.find(val.getQualifiedName());
                            if (traceDistance != null) {
                                // set the ne TraceDistance instance name.
                                property.setValue(val.getQualifiedName());
                                strategy.setTraceDistance(traceDistance);
                                if (index < 0 || index >= propertySets.size()) {
                                    propertySets.add(traceDistance.getPropertySet());
                                } else {
                                    propertySets.add(traceDistance.getPropertySet());
                                }
                            }
                        } catch (DescriptorFactory.InstanceNotFountException ex) {
                            LOG.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        }

        private class MergeHandlerProperty extends ClassProperty {

            private final de.fub.maps.project.aggregator.xml.Property property;
            private final PropertySection section;
            private ClassWrapper wrapper;

            public MergeHandlerProperty(PropertySection propertySection, de.fub.maps.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), MergeHandler.class);
                this.property = property;
                this.section = propertySection;
                wrapper = strategy.getBaseMergeHandler() != null
                        ? new ClassWrapper(strategy.getBaseMergeHandler().getClass())
                        : new ClassWrapper(MergeHandler.class);
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (strategy.getAggregator().getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    if (val == null) {
                        throw new IllegalArgumentException("null is not a valid argument.");
                    } else if (wrapper == null || !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                        ClassWrapper oldValue = this.wrapper;
                        wrapper = val;
                        int index = -1;
                        if (oldValue != null) {
                            for (de.fub.maps.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                                index++;
                                if (oldValue.getQualifiedName().equals(propertySet.getId())) {
                                    LOG.info(MessageFormat.format("PropertySet: {0} removed: {1}", propertySet.getId(), section.getPropertySet().remove(propertySet)));
                                    break;
                                }
                            }
                        }
                        try {
                            MergeHandler mergeHandler = MergeHandler.Factory.find(val.getQualifiedName());
                            if (mergeHandler != null) {
                                strategy.setBaseMergeHandler(mergeHandler);
                                property.setValue(val.getQualifiedName());
                                if (index < 0 || index >= section.getPropertySet().size()) {
                                    section.getPropertySet().add(mergeHandler.getPropertySet());
                                } else {
                                    section.getPropertySet().add(index, mergeHandler.getPropertySet());
                                }
                            }
                        } catch (DescriptorFactory.InstanceNotFountException ex) {
                            LOG.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        }

    }
}
