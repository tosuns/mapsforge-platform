/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper;

import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassProperty;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassWrapper;
import de.fub.mapsforge.project.aggregator.pipeline.processes.AggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.MergeHandler;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.TraceDistance;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.AggregationStrategy;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * This is only a wrapper class and its only usage is to annotate this class and
 * keep the agg2graph module clean of the netbeans api.
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "DefaultAggregationStrategy_Name=Default Aggregation Strategy",
    "DefaultAggregationStrategy_Description=The standard aggregation strategy implementation",
    "DefaultAggregationStrategy_Settings_Name=Settings",
    "DefaultAggregationStrategy_Settings_Description=All parameters to configure the DefaultAggregationStrategy.",
    "DefaultAggregationStrategy_MaxLookahead_Name=Max Lookahead",
    "DefaultAggregationStrategy_MaxLookahead_Description=No description available",
    "DefaultAggregationStrategy_MaxPathDifference_Name=Max Path Difference",
    "DefaultAggregationStrategy_MaxPathDifference_Description=No description available",
    "DefaultAggregationStrategy_MaxInitDistance_Name=Max Init Distance",
    "DefaultAggregationStrategy_MaxInitDistance_Description=No description available",
    "DefaultAggregationStrategy_MergaHandler_Type_Name=MergeHandler Instance Type",
    "DefaultAggregationStrategy_MergeHandler_Type_Description=The instance that is responsible for merging the data points",
    "DefaultAggregationStrategy_TraceDistance_Type_Name=TraceDistance Instance Type",
    "DefaultAggregationStrategy_TraceDistance_Type_Description=The instance which is responsible for tracing the distance of a segment."
})
@ServiceProvider(service = AggregationStrategy.class)
public class DefaultAggregationStrategy extends de.fub.agg2graph.agg.strategy.DefaultAggregationStrategy implements AggregationStrategy {

    private static final String PROP_NAME_MAX_LOOKAHEAD = "default.aggregation.strategy.max.lookahead";
    private static final String PROP_NAME_MAX_PATH_DIFFERENCE = "default.aggregation.strategy.max.path.difference";
    private static final String PROP_NAME_MAX_INIT_DISTANCE = "default.aggregation.strategy.max.init.distance";
    private static final String PROP_NAME_BASE_MERGEHANDLER_TYPE = "default.aggregation.strategy.base.mergehandler.type";
    private static final String PROP_NAME_TRACE_DISTANCE_TYPE = "default.aggregation.strategy.trace.distance.type";
    private PropertySection propertySection = null;
    private Aggregator aggregator;
    private Node nodeDelegate = null;

    public DefaultAggregationStrategy() {
    }

    private void reInit() {
        propertySection = null;
        propertySection = getPropertySection();

        if (propertySection != null) {
            List<PropertySet> propertySets = propertySection.getPropertySet();
            for (PropertySet propertySet : propertySets) {
                if (Bundle.DefaultAggregationStrategy_Name().equals(propertySet.getName())) {
                    for (Property property : propertySet.getProperties()) {
                        if (property.getValue() != null) {
                            if (PROP_NAME_MAX_LOOKAHEAD.equals(property.getId())) {
                                setMaxLookahead(Integer.parseInt(property.getValue()));
                            } else if (PROP_NAME_MAX_PATH_DIFFERENCE.equals(property.getId())) {
                                setMaxPathDifference(Double.parseDouble(property.getValue()));
                            } else if (PROP_NAME_MAX_INIT_DISTANCE.equals(property.getId())) {
                                setMaxInitDistance(Double.parseDouble(property.getValue()));
                            } else if (PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                                // create the specified base MergeHandler
                                MergeHandler mHandler = MergeHandler.Factory.find(property.getValue(), aggregator);
                                // in case the specified qualified name does not exist
                                // create the default merge handler with default setting
                                if (mHandler == null) {
                                    mHandler = new DefaultMergeHandler();
                                }
                                baseMergeHandler = mHandler;
                            } else if (PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
                                // create the specified base TraceDistance instance
                                TraceDistance tDistance = TraceDistance.Factory.find(property.getValue(), aggregator);
                                // in case the specified qualified name does not exist
                                // create the default TraceDistance instance with default setting
                                if (tDistance == null) {
                                    tDistance = new DefaultTraceDistance();
                                }
                                traceDistance = tDistance;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        if (aggregator != null) {
            setAggContainer(getAggregator().getAggContainer());
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
            nodeDelegate = new AggregationStrategyNode(DefaultAggregationStrategy.this);
        }
        return nodeDelegate;
    }

    @Override
    public PropertySection getPropertySection() {
        if (propertySection == null) {
            if (getAggregator() != null) {
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (descriptor != null
                            && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        for (PropertySection section : descriptor.getProperties().getSections()) {
                            if (Bundle.DefaultAggregationStrategy_Name().equals(section.getName())) {
                                propertySection = section;
                                break;
                            }
                        }
                    }
                }
                if (propertySection == null) {
                    propertySection = createDefaultDescriptor();
                }
            } else {
                propertySection = createDefaultDescriptor();
            }
        }
        return propertySection;
    }

    private PropertySection createDefaultDescriptor() {

        PropertySet propertySet = new PropertySet(
                Bundle.DefaultAggregationStrategy_Settings_Name(),
                Bundle.DefaultAggregationStrategy_Settings_Description());

        Property property = new Property();
        property.setName(Bundle.DefaultAggregationStrategy_MaxLookahead_Name());
        property.setDescription(Bundle.DefaultAggregationStrategy_MaxLookahead_Description());
        property.setId(PROP_NAME_MAX_LOOKAHEAD);
        property.setJavaType(Integer.class.getName());
        property.setValue(String.valueOf(getMaxLookahead()));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.DefaultAggregationStrategy_MaxPathDifference_Name());
        property.setDescription(Bundle.DefaultAggregationStrategy_MaxPathDifference_Description());
        property.setId(PROP_NAME_MAX_PATH_DIFFERENCE);
        property.setJavaType(Double.class.getName());
        property.setValue(String.format(Locale.ENGLISH, "%f", getMaxPathDifference()));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.DefaultAggregationStrategy_MaxInitDistance_Name());
        property.setDescription(Bundle.DefaultAggregationStrategy_MaxInitDistance_Description());
        property.setId(PROP_NAME_MAX_INIT_DISTANCE);
        property.setJavaType(Double.class.getName());
        property.setValue(String.format(Locale.ENGLISH, "%f", getMaxInitDistance()));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.DefaultAggregationStrategy_MergaHandler_Type_Name());
        property.setDescription(Bundle.DefaultAggregationStrategy_MergeHandler_Type_Description());
        property.setId(PROP_NAME_BASE_MERGEHANDLER_TYPE);
        property.setJavaType(String.class.getName());
        property.setValue(DefaultMergeHandler.class.getName());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setName(Bundle.DefaultAggregationStrategy_TraceDistance_Type_Name());
        property.setDescription(Bundle.DefaultAggregationStrategy_TraceDistance_Type_Description());
        property.setId(PROP_NAME_TRACE_DISTANCE_TYPE);
        property.setJavaType(String.class.getName());
        property.setValue(DefaultTraceDistance.class.getName());
        propertySet.getProperties().add(property);

        PropertySection section = new PropertySection(
                Bundle.DefaultAggregationStrategy_Settings_Name(),
                Bundle.DefaultAggregationStrategy_Settings_Description());

        section.getPropertySet().add(propertySet);

        MergeHandler handler = MergeHandler.Factory.find(DefaultMergeHandler.class.getName());
        if (handler != null) {
            PropertySet desc = handler.getPropertySet();
            if (desc != null) {
                section.getPropertySet().add(desc);
            }
        }
        TraceDistance traceHandler = TraceDistance.Factory.find(DefaultTraceDistance.class.getName());
        if (traceHandler != null) {
            PropertySet desc = traceHandler.getPropertySet();
            if (desc != null) {
                section.getPropertySet().add(desc);
            }
        }

        return section;
    }

    private static class AggregationStrategyNode extends AbstractNode implements ChangeListener {

        private final DefaultAggregationStrategy strategy;
        private ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

        public AggregationStrategyNode(DefaultAggregationStrategy strategy) {
            super(Children.LEAF);
            this.strategy = strategy;
            if (this.strategy != null && this.strategy.getAggregator() != null) {
                modelSynchronizerClient = this.strategy.getAggregator().create(AggregationStrategyNode.this);
            }
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = Sheet.createPropertiesSet();
            sheet.put(set);
            if (strategy != null) {
                PropertySection propertySection = strategy.getPropertySection();

                if (propertySection != null) {

                    for (final de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : propertySection.getPropertySet()) {
                        set.setDisplayName(propertySet.getName());
                        set.setName(propertySet.getName());
                        set.setShortDescription(propertySet.getDescription());

                        List<de.fub.mapsforge.project.aggregator.xml.Property> properties = propertySet.getProperties();
                        for (final de.fub.mapsforge.project.aggregator.xml.Property property : properties) {

                            if (PROP_NAME_MAX_LOOKAHEAD.equals(property.getId())) {
                                set.put(new ProcessProperty(modelSynchronizerClient, property));
                            } else if (PROP_NAME_MAX_PATH_DIFFERENCE.equals(property.getId())) {
                                set.put(new ProcessProperty(modelSynchronizerClient, property));
                            } else if (PROP_NAME_MAX_INIT_DISTANCE.equals(property.getId())) {
                                set.put(new ProcessProperty(modelSynchronizerClient, property));
                            } else if (PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                                ClassProperty classProperty = new ClassProperty(property.getId(), property.getName(), property.getDescription(), MergeHandler.class) {
                                    private ClassWrapper wrapper = strategy.getBaseMergeHandler() != null
                                            ? new ClassWrapper(strategy.getBaseMergeHandler().getClass())
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
                                            }
                                            wrapper = val;
                                            property.setValue(val.getQualifiedName());
                                            modelSynchronizerClient.modelChangedFromGui();
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
                                            }
                                            wrapper = val;
                                            property.setValue(val.getQualifiedName());
                                            modelSynchronizerClient.modelChangedFromGui();
                                        }
                                    }
                                };
                                set.put(classProperty);
                            }
                        }
                    }
                }

                IMergeHandler baseMergeHandler = strategy.getBaseMergeHandler();
                if (baseMergeHandler instanceof MergeHandler) {
                    MergeHandler mergeHandler = (MergeHandler) baseMergeHandler;
                    Node nodeDelegate = mergeHandler.getNodeDelegate();
                    PropertySet[] propertySets = nodeDelegate.getPropertySets();
                    for (PropertySet propertySet : propertySets) {
                        set = Sheet.createPropertiesSet();
                        set.setName(propertySet.getName());
                        set.setDisplayName(propertySet.getDisplayName());
                        set.setShortDescription(propertySet.getShortDescription());
                        for (Property<?> nodeProperty : propertySet.getProperties()) {
                            set.put(nodeProperty);
                        }
                    }
                }
                ITraceDistance traceDist = strategy.getTraceDist();
                if (traceDist instanceof TraceDistance) {
                    TraceDistance traceDistance = (TraceDistance) traceDist;
                    Node nodeDelegate1 = traceDistance.getNodeDelegate();
                    PropertySet[] propertySets = nodeDelegate1.getPropertySets();
                    for (PropertySet propertySet : propertySets) {
                        set = Sheet.createPropertiesSet();
                        set.setName(propertySet.getName());
                        set.setDisplayName(propertySet.getDisplayName());
                        set.setShortDescription(propertySet.getShortDescription());
                        for (Property<?> nodeProperty : propertySet.getProperties()) {
                            set.put(nodeProperty);
                        }
                    }
                }
            }
            return sheet;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            // do nothing,only writy operations will be done
        }
    }
}
