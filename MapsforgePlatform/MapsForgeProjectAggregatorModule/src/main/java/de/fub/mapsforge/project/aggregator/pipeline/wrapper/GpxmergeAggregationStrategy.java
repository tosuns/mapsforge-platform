/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper;

import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.mapsforge.project.aggregator.pipeline.processes.AggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.AggregationStrategy;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.MergeHandler;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.TraceDistance;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    "GpxmergeAggregationStrategy_Name=Gpx Aggregation Strategy",
    "GpxmergeAggregationStrategy_Description=No description available",
    "GpxmergeAggregationStrategy_Settings_Name=Setteings",
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
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (descriptor != null
                            && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        List<PropertySection> sections = descriptor.getProperties().getSections();
                        for (PropertySection section : sections) {
                            if (Bundle.GpxmergeAggregationStrategy_Name().equals(section.getName())) {
                                propertySection = section;
                                break;
                            }
                        }
                    }
                }
                if (propertySection == null) {
                    propertySection = createDefaultPropertySection();
                }
            } else {
                propertySection = createDefaultPropertySection();
            }
        }
        return propertySection;
    }

    private PropertySection createDefaultPropertySection() {

        PropertySet propertySet = new PropertySet(
                Bundle.GpxmergeAggregationStrategy_Settings_Name(),
                Bundle.GpxmergeAggregationStrategy_Settings_Description());

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

        section.getPropertySet().add(propertySet);

        MergeHandler handler = MergeHandler.Factory.find(DefaultMergeHandler.class.getName());
        if (handler != null) {
            PropertySet set = handler.getPropertySet();
            if (set != null) {
                section.getPropertySet().add(set);
            }
        }
        TraceDistance traceHandler = TraceDistance.Factory.find(GpxmergeTraceDistance.class.getName());
        if (traceHandler != null) {
            PropertySet set = traceHandler.getPropertySet();
            if (set != null) {
                section.getPropertySet().add(set);
            }
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
                        if (PROP_NAME_MAX_INIT_DISTANCE.equals(property.getId())) {
                            setMaxInitDistance(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MAX_PATH_DIFFERENCE.equals(property.getId())) {
                            setMaxPathDifference(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_BASE_MERGEHANDLER_TYPE.equals(property.getId())) {
                            MergeHandler handler = MergeHandler.Factory.find(property.getValue(), getAggregator());
                            if (handler == null) {
                                handler = new DefaultMergeHandler();
                                handler.setAggregator(getAggregator());
                            }
                            baseMergeHandler = handler;
                        } else if (PROP_NAME_TRACE_DISTANCE_TYPE.equals(property.getId())) {
                            TraceDistance traceDistanceHandler = TraceDistance.Factory.find(property.getValue(), getAggregator());
                            if (traceDistanceHandler == null) {
                                traceDistanceHandler = new GpxmergeTraceDistance();
                                traceDistanceHandler.setAggregator(aggregator);
                            }
                            traceDistance = traceDistanceHandler;
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

    private static class GpxAggregationStrategyNode extends AbstractNode implements ChangeListener {

        private final GpxmergeAggregationStrategy strategy;
        private ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

        public GpxAggregationStrategyNode(GpxmergeAggregationStrategy strategy) {
            super(Children.LEAF);
            this.strategy = strategy;
            if (this.strategy != null && this.strategy.getAggregator() != null) {
                modelSynchronizerClient = this.strategy.getAggregator().create(GpxAggregationStrategyNode.this);
            }
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();

            if (this.strategy != null) {
                PropertySection propertySection = this.strategy.getPropertySection();
                if (propertySection != null) {

                    for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : propertySection.getPropertySet()) {
                        Sheet.Set set = Sheet.createPropertiesSet();
                        set.setName(propertySet.getName());
                        set.setDisplayName(propertySet.getName());
                        set.setShortDescription(propertySet.getDescription());
                        sheet.put(set);

                        for (de.fub.mapsforge.project.aggregator.xml.Property property : propertySet.getProperties()) {
                            set.put(new ProcessProperty(modelSynchronizerClient, property));
                        }
                    }


                    IMergeHandler baseMergeHandler = strategy.getBaseMergeHandler();
                    if (baseMergeHandler instanceof MergeHandler) {
                        MergeHandler mergeHandler = (MergeHandler) baseMergeHandler;
                        PropertySet[] propertySets = mergeHandler.getNodeDelegate().getPropertySets();

                        for (PropertySet propertySet : propertySets) {
                            Sheet.Set set = Sheet.createPropertiesSet();
                            set.setName(propertySet.getName());
                            set.setDisplayName(propertySet.getDisplayName());
                            set.setShortDescription(propertySet.getShortDescription());
                            sheet.put(set);

                            for (Property<?> nodeProperty : propertySet.getProperties()) {
                                set.put(nodeProperty);
                            }
                        }
                    }
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

        @Override
        public void stateChanged(ChangeEvent e) {
            // do nothing
        }
    }
}
