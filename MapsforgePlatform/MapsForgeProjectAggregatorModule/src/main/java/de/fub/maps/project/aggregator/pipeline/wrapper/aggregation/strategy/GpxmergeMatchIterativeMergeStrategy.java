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
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "GpxmergeMatchIterativeMergeStrategy_Name=Gpx Merge Match Iterative Merge Strategy",
    "GpxmergeMatchIterativeMergeStrategy_Description=The standard aggregation strategy implementation",
    "GpxmergeMatchIterativeMergeStrategy_Settings_Name=Gpx Merge Match Iterative Merge Strategy Settings",
    "GpxmergeMatchIterativeMergeStrategy_Settings_Description=All parameters to configure the GpxmergeMatchIterativeMergeStrategy.",
    "GpxmergeMatchIterativeMergeStrategy_MaxLookahead_Name=Max Lookahead",
    "GpxmergeMatchIterativeMergeStrategy_MaxLookahead_Description=No description available",
    "GpxmergeMatchIterativeMergeStrategy_MaxPathDifference_Name=Max Path Difference",
    "GpxmergeMatchIterativeMergeStrategy_MaxPathDifference_Description=No description available",
    "GpxmergeMatchIterativeMergeStrategy_MaxInitDistance_Name=Max Init Distance",
    "GpxmergeMatchIterativeMergeStrategy_MaxInitDistance_Description=No description available",
    "GpxmergeMatchIterativeMergeStrategy_MergaHandler_Type_Name=MergeHandler Instance Type",
    "GpxmergeMatchIterativeMergeStrategy_MergeHandler_Type_Description=The instance that is responsible for merging the data points",
    "GpxmergeMatchIterativeMergeStrategy_TraceDistance_Type_Name=TraceDistance Instance Type",
    "GpxmergeMatchIterativeMergeStrategy_TraceDistance_Type_Description=The instance which is responsible for tracing the distance of a segment."
})
@ServiceProvider(service = AggregationStrategy.class)
public class GpxmergeMatchIterativeMergeStrategy extends de.fub.agg2graph.agg.strategy.GpxmergeMatchIterativeMergeStrategy implements AggregationStrategy {

    private static final Logger LOG = Logger.getLogger(GpxmergeMatchIterativeMergeStrategy.class.getName());
    private static final String PROP_NAME_MAX_LOOKAHEAD = "GpxmergeMatchIterativeMergeStrategy.max.lookahead";
    private static final String PROP_NAME_MAX_PATHDIFFERENCE = "GpxmergeMatchIterativeMergeStrategy.max.pathdifference";
    private static final String PROP_NAME_MAX_INITDISTANCE = "GpxmergeMatchIterativeMergeStrategy.max.initDistance";
    private static final String PROP_NAME_BASE_MERGEHANDLER_TYPE = "GpxmergeMatchIterativeMergeStrategy.base.mergehandler.type";
    private static final String PROP_NAME_TRACE_DISTANCE_TYPE = "GpxmergeMatchIterativeMergeStrategy.trace.distance.type";
    protected PropertySection propertySection = null;
    protected Aggregator aggregator;
    protected Node nodeDelegate = null;

    public GpxmergeMatchIterativeMergeStrategy() {
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
            nodeDelegate = new StrategyNode(GpxmergeMatchIterativeMergeStrategy.this);
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
                            if (GpxmergeMatchIterativeMergeStrategy.class.getName().equals(section.getId())) {
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
                Bundle.GpxmergeMatchIterativeMergeStrategy_Settings_Name(),
                Bundle.GpxmergeMatchIterativeMergeStrategy_Settings_Description());
        propertySet.setId(GpxmergeMatchIterativeMergeStrategy.class.getName());

        Property property = new Property();
        property.setName(Bundle.GpxmergeMatchIterativeMergeStrategy_MaxLookahead_Name());
        property.setDescription(Bundle.GpxmergeMatchIterativeMergeStrategy_MaxLookahead_Description());
        property.setId(PROP_NAME_MAX_LOOKAHEAD);
        property.setJavaType(Integer.class.getName());
        property.setValue(MessageFormat.format("{0}", getMaxLookahead()).replaceAll("[^0-9]", ""));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.GpxmergeMatchIterativeMergeStrategy_MaxPathDifference_Name());
        property.setDescription(Bundle.GpxmergeMatchIterativeMergeStrategy_MaxPathDifference_Description());
        property.setId(PROP_NAME_MAX_PATHDIFFERENCE);
        property.setJavaType(Double.class.getName());
        property.setValue(MessageFormat.format("{0}", getMaxLookahead()).replaceAll("\\,", "."));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.GpxmergeMatchIterativeMergeStrategy_MaxInitDistance_Name());
        property.setDescription(Bundle.GpxmergeMatchIterativeMergeStrategy_MaxInitDistance_Description());
        property.setId(PROP_NAME_MAX_INITDISTANCE);
        property.setJavaType(Double.class.getName());
        property.setValue(MessageFormat.format("{0}", getMaxLookahead()).replaceAll("\\,", "."));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.GpxmergeMatchIterativeMergeStrategy_MergaHandler_Type_Name());
        property.setDescription(Bundle.GpxmergeMatchIterativeMergeStrategy_MergeHandler_Type_Description());
        property.setId(PROP_NAME_BASE_MERGEHANDLER_TYPE);
        property.setJavaType(String.class.getName());
        property.setValue(IterativeClosestPointsMerge.class.getName());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.GpxmergeMatchIterativeMergeStrategy_TraceDistance_Type_Name());
        property.setDescription(Bundle.GpxmergeMatchIterativeMergeStrategy_TraceDistance_Type_Description());
        property.setId(PROP_NAME_TRACE_DISTANCE_TYPE);
        property.setJavaType(String.class.getName());
        property.setValue(GpxmergeTraceDistance.class.getName());
        propertySet.getProperties().add(property);

        PropertySection section = new PropertySection(
                Bundle.GpxmergeMatchIterativeMergeStrategy_Settings_Name(),
                Bundle.GpxmergeMatchIterativeMergeStrategy_Settings_Description());
        section.setId(GpxmergeMatchIterativeMergeStrategy.class.getName());

        section.getPropertySet().add(propertySet);

        MergeHandler handler;
        try {
            handler = MergeHandler.Factory.find(IterativeClosestPointsMerge.class.getName());
            if (handler != null) {
                PropertySet desc = handler.getPropertySet();
                if (desc != null) {
                    section.getPropertySet().add(desc);
                }
            }
        } catch (DescriptorFactory.InstanceNotFountException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        TraceDistance traceHandler;
        try {
            traceHandler = TraceDistance.Factory.find(GpxmergeTraceDistance.class.getName());
            if (traceHandler != null) {
                PropertySet desc = traceHandler.getPropertySet();
                if (desc != null) {
                    section.getPropertySet().add(desc);
                }
            }
        } catch (DescriptorFactory.InstanceNotFountException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return section;
    }

    protected void reInit() {
        propertySection = null;
        propertySection = getPropertySection();
        if (propertySection != null) {
            List<PropertySet> propertySets = propertySection.getPropertySet();
            for (PropertySet propertySet : propertySets) {
                if (Bundle.GpxmergeMatchIterativeMergeStrategy_Settings_Name().equals(propertySet.getName())) {
                    for (Property property : propertySet.getProperties()) {
                        if (property.getValue() != null) {
                            if (PROP_NAME_MAX_LOOKAHEAD.equals(property.getId())) {
                                setMaxLookahead(Integer.parseInt(property.getValue()));
                            } else if (PROP_NAME_MAX_PATHDIFFERENCE.equals(property.getId())) {
                                setMaxPathDifference(Double.parseDouble(property.getValue()));
                            } else if (PROP_NAME_MAX_INITDISTANCE.equals(property.getId())) {
                                setMaxInitDistance(Double.parseDouble(property.getValue()));
                            } else if (PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                                // create the specified base MergeHandler
                                MergeHandler mHandler = null;
                                try {
                                    mHandler = MergeHandler.Factory.find(property.getValue(), aggregator);
                                } catch (DescriptorFactory.InstanceNotFountException ex) {
                                    try {
                                        mHandler = MergeHandler.Factory.getDefault();
                                        mHandler.setAggregator(aggregator);
                                    } catch (DescriptorFactory.InstanceNotFountException ex1) {
                                        Exceptions.printStackTrace(ex1);
                                    }
                                }
                                baseMergeHandler = mHandler;
                            } else if (PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
                                // create the specified base TraceDistance instance
                                TraceDistance tDistance = null;
                                try {
                                    tDistance = TraceDistance.Factory.find(property.getValue(), aggregator);
                                } catch (DescriptorFactory.InstanceNotFountException ex) {
                                    try {
                                        tDistance = TraceDistance.Factory.getDefault();
                                        tDistance.setAggregator(aggregator);
                                    } catch (DescriptorFactory.InstanceNotFountException ex1) {
                                        Exceptions.printStackTrace(ex1);
                                    }
                                }
                                traceDistance = tDistance;
                            }
                        }
                    }
                }
            }
        }
    }

    private static class StrategyNode extends AbstractNode {

        private static final Logger LOG = Logger.getLogger(StrategyNode.class.getName());
        private final GpxmergeMatchIterativeMergeStrategy strategy;

        public StrategyNode(GpxmergeMatchIterativeMergeStrategy strategy) {
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
                        if (GpxmergeMatchIterativeMergeStrategy.class.getName().equals(propertySet.getId())) {
                            Sheet.Set set = Sheet.createPropertiesSet();
                            sheet.put(set);
                            set.setName(propertySet.getId());
                            set.setDisplayName(propertySet.getName());
                            set.setShortDescription(propertySet.getDescription());
                            List<de.fub.maps.project.aggregator.xml.Property> properties = propertySet.getProperties();
                            for (final de.fub.maps.project.aggregator.xml.Property property : properties) {
                                if (GpxmergeMatchIterativeMergeStrategy.PROP_NAME_MAX_LOOKAHEAD.equals(property.getId())) {
                                    set.put(new NodeProperty(property));
                                } else if (GpxmergeMatchIterativeMergeStrategy.PROP_NAME_MAX_PATHDIFFERENCE.equals(property.getId())) {
                                    set.put(new NodeProperty(property));
                                } else if (GpxmergeMatchIterativeMergeStrategy.PROP_NAME_MAX_INITDISTANCE.equals(property.getId())) {
                                    set.put(new NodeProperty(property));
                                } else if (GpxmergeMatchIterativeMergeStrategy.PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                                    ClassProperty classProperty = new MergeHandlerProperty(propertySection, property);
                                    set.put(classProperty);
                                } else if (GpxmergeMatchIterativeMergeStrategy.PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
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
            private ClassWrapper wrapper = strategy.getTraceDist() != null
                    ? new ClassWrapper(strategy.getTraceDist().getClass())
                    : new ClassWrapper(TraceDistance.class);

            public TractDistanceProperty(PropertySection propertySection, de.fub.maps.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), TraceDistance.class);
                this.propertySection = propertySection;
                this.property = property;
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
