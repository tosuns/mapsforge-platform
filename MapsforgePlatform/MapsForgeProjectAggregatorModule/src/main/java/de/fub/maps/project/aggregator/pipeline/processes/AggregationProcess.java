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
package de.fub.maps.project.aggregator.pipeline.processes;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.PointGhostPointPair;
import de.fub.agg2graph.agg.strategy.AbstractAggregationStrategy;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graphui.layers.AggContainerLayer;
import de.fub.agg2graphui.layers.MatchingLayer;
import de.fub.agg2graphui.layers.MergingLayer;
import de.fub.maps.project.aggregator.factories.nodes.properties.ClassProperty;
import de.fub.maps.project.aggregator.factories.nodes.properties.ClassWrapper;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.pipeline.AggregationProcessNode;
import de.fub.maps.project.aggregator.pipeline.wrapper.DefaultCachingStrategy;
import de.fub.maps.project.aggregator.pipeline.wrapper.aggregation.strategy.DefaultAggregationStrategy;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.AggregationStrategy;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.CachingStrategy;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.DescriptorFactory;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.api.process.ProcessPipeline;
import de.fub.maps.project.api.statistics.StatisticProvider;
import de.fub.maps.project.models.Aggregator;
import java.awt.Component;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Process unit implementations, which handles the aggregation job of an
 * Aggregator instance.
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
public class AggregationProcess extends AbstractAggregationProcess<List<GPSSegment>, AggContainer> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private static final Logger LOG = Logger.getLogger(AggregationProcess.class.getName());
    private List<GPSSegment> inputList;
    private MergingLayer mergeLayer;
    private MatchingLayer matchingLayer;
    private AggContainerLayer aggregationLayer;
    private int totalAggNodeCount = 0;
    private int totalGPSPointCount = 0;
    private int totalPointGhostPointPairs = 0;
    private AggregationNode node;

    public AggregationProcess() {
        initLayers();
    }

    @Override
    public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        super.setProcessDescriptor(processDescriptor);
        reInit();
    }

    private void reInit() {
        node = null;
        if (getProcessDescriptor() != null) {
            AggregatorDescriptor aggregatorDescriptor = getAggregator().getAggregatorDescriptor();
            if (aggregatorDescriptor != null) {
                if (aggregatorDescriptor.getAggregationStrategy() != null) {
                    AggregationStrategy aggregationStrategy = null;
                    try {
                        aggregationStrategy = AggregationStrategy.Factory.find(aggregatorDescriptor.getAggregationStrategy(), getAggregator());

                    } catch (DescriptorFactory.InstanceNotFountException ex) {
                        try {
                            aggregationStrategy = AggregationStrategy.Factory.getDefault();
                        } catch (DescriptorFactory.InstanceNotFountException ex1) {
                            Exceptions.printStackTrace(ex1);
                        }
                    }
                    if (aggregationStrategy != null
                            && getAggregator() != null
                            && getAggregator().getAggContainer() != null) {
                        getAggregator().getAggContainer().setAggregationStrategy(aggregationStrategy);
                    }
                }
                if (aggregatorDescriptor.getTileCachingStrategy() != null) {
                    CachingStrategy cachingStrategy = null;
                    try {
                        cachingStrategy = CachingStrategy.Factory.find(aggregatorDescriptor.getTileCachingStrategy(), getAggregator());
                    } catch (DescriptorFactory.InstanceNotFountException ex) {
                        try {
                            cachingStrategy = CachingStrategy.Factory.getDefault();
                        } catch (DescriptorFactory.InstanceNotFountException ex1) {
                            Exceptions.printStackTrace(ex1);
                        }
                    }
                    if (cachingStrategy != null
                            && getAggregator() != null
                            && getAggregator().getAggContainer() != null) {
                        getAggregator().getAggContainer().setCachingStrategy(cachingStrategy);
                    }
                }
            }
        }
    }

    private void initLayers() {
        mergeLayer = new MergingLayer();
        matchingLayer = new MatchingLayer();
        aggregationLayer = new AggContainerLayer();

        getLayers().add(matchingLayer);
        getLayers().add(mergeLayer);
        getLayers().add(aggregationLayer);
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        // for the  default settings the call will be delegated to the default
        // AggregationStrategy instance DefaulAggregationStrategy

        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(AggregationProcess.class.getName());
        descriptor.setDescription(Bundle.CLT_AggregationProcess_Description());
        descriptor.setDisplayName(Bundle.CLT_AggregationProcess_Name());

        DefaultAggregationStrategy defaultAggregationStrategy = new DefaultAggregationStrategy();

        defaultAggregationStrategy.setAggregator(getAggregator());
        PropertySection propertySection = defaultAggregationStrategy.getPropertySection();
        if (propertySection != null) {
            descriptor.getProperties().getSections().add(propertySection);
        }

        DefaultCachingStrategy defaultCachingStrategy = new DefaultCachingStrategy();
        defaultCachingStrategy.setAggregator(getAggregator());
        propertySection = defaultCachingStrategy.getPropertySection();
        if (propertySection != null) {
            descriptor.getProperties().getSections().add(propertySection);
        }

        return descriptor;
    }

    @Override
    public Node getNodeDelegate() {
        if (node == null) {
            node = new AggregationNode(AggregationProcess.this);
        }
        return node;
    }

    @Override
    public void setInput(List<GPSSegment> input) {
        this.inputList = input;
    }

    @Override
    public AggContainer getResult() {
        synchronized (RUN_MUTEX) {
            this.inputList = null;
            return getAggregator().getAggContainer();
        }
    }

    @Override
    protected void start() {
        totalAggNodeCount = 0;
        totalGPSPointCount = 0;
        totalPointGhostPointPairs = 0;

        if (inputList != null
                && getAggregator() != null
                && getAggregator().getAggContainer() != null) {
            LOG.log(Level.FINE, "Segment size: {0}", inputList.size());
            ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
            handle.start(inputList.size());
            try {

                aggregationLayer.clearRenderObjects();
                mergeLayer.clearRenderObjects();
                matchingLayer.clearRenderObjects();

                AggContainer aggContainer = getAggregator().getAggContainer();

                int counter = 0;

                LOG.log(Level.FINE, "clean segments: {0}", inputList.size());

                for (GPSSegment inputSegment : inputList) {
                    if (canceled.get()) {
                        fireProcessCanceledEvent();
                        break;
                    }

                    counter++;
                    aggContainer.addSegment(inputSegment);

                    // update debug layers: matching, merging
                    IAggregationStrategy aggregationStrategy = aggContainer.getAggregationStrategy();

                    if (aggregationStrategy instanceof AbstractAggregationStrategy) {
                        AbstractAggregationStrategy abstractAggregationStrategy = (AbstractAggregationStrategy) aggregationStrategy;
                        IMergeHandler mergeHandler = abstractAggregationStrategy.getMergeHandler();

                        if (mergeHandler != null) {

                            List<AggNode> aggNodes = mergeHandler.getAggNodes();
                            if (aggNodes != null) {
                                totalAggNodeCount += aggNodes.size();
                                matchingLayer.add(aggNodes);
                            }

                            List<GPSPoint> gpsPoints = mergeHandler.getGpsPoints();
                            if (gpsPoints != null) {
                                totalGPSPointCount += gpsPoints.size();
                                matchingLayer.add(gpsPoints);
                            }

                            List<PointGhostPointPair> pointGhostPointPairs = mergeHandler.getPointGhostPointPairs();
                            if (pointGhostPointPairs != null) {
                                for (PointGhostPointPair pgpp : pointGhostPointPairs) {
                                    List<ILocation> line = new ArrayList<ILocation>(2);
                                    line.add(new GPSPoint(pgpp.point));
                                    line.add(new GPSPoint(pgpp.ghostPoint));
                                    mergeLayer.add(line);
                                    totalPointGhostPointPairs++;
                                }
                            }
                        }
                    }

                    fireProcessProgressEvent(new ProcessPipeline.ProcessEvent<AggregationProcess>(this, "Aggregation...", (int) ((100d / inputList.size()) * (counter))));
                    handle.progress(counter);
                    LOG.log(Level.FINE, "Segment number: {0}", (counter));
                }

                aggregationLayer.add(aggContainer);
            } finally {
                handle.finish();
            }

        }
    }

    @NbBundle.Messages("CLT_AggregationProcess_Name=Aggregation")
    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return Bundle.CLT_AggregationProcess_Name();
    }

    @NbBundle.Messages("CLT_AggregationProcess_Description=Aggregation process")
    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_AggregationProcess_Description();
    }

    @Override
    public Image getIcon() {
        return IMAGE;
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException {
        List<StatisticSection> statisticSections = new ArrayList<StatisticProvider.StatisticSection>();
        statisticSections.add(getPerformanceData());

        StatisticSection section = new StatisticSection(
                "Aggregation Statistics",
                "Displays all statistics data which are computed during the aggregation.");
        statisticSections.add(section);

        section.getStatisticsItemList().add(new StatisticItem(
                "Total Aggregation Node Count",
                String.valueOf(totalAggNodeCount),
                "The total amount of aggreagtion nodes, which are created during this aggregation process."));

        section.getStatisticsItemList().add(new StatisticItem(
                "Total GPS Point Count",
                String.valueOf(totalGPSPointCount),
                "The total amount of GPS Point that are added during the aggregation."));

        section.getStatisticsItemList().add(new StatisticItem(
                "Total Count Point/GhostPoint Pairs ",
                String.valueOf(totalPointGhostPointPairs),
                "The total amount of paris of Point/Ghostpoint."));

        return statisticSections;
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return canceled.get();
    }

    @Override
    public Component getVisualRepresentation() {
        return null;

    }

    @NbBundle.Messages({
        "CLT_Property_Aggregator_Strategy_Class_Name=Aggregation Strategy",
        "CLT_Property_Aggregator_Strategy_Class_Description=The aggregation strategy that wil be used during aggregation.",
        "CLT_Property_TileCache_Strategy_Class_Name=Tilecache Strategy",
        "CLT_Property_TileCache_Strategy_Class_Description=The TileCache strategy which will be used during the aggregation."
    })
    private static class AggregationNode extends AggregationProcessNode {

        private final AggregationProcess aggregationProcess;

        public AggregationNode(AggregationProcess process) {
            super(process);
            this.aggregationProcess = process;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            final Aggregator aggregator = aggregationProcess.getAggregator();
            if (aggregator != null
                    && aggregator.getAggContainer() != null
                    && aggregator.getAggContainer().getAggregationStrategy() instanceof AggregationStrategy) {

                sheet = Sheet.createDefault();
                PropertySet[] sets = sheet.toArray();
                for (PropertySet set : sets) {
                    sheet.remove(set.getName());
                }
                AggregatorDescriptor aggregatorDescriptor = aggregator.getAggregatorDescriptor();
                if (aggregatorDescriptor != null) {

                    Sheet.Set nodeSet = Sheet.createPropertiesSet();
                    nodeSet.setName(AggregationProcess.class.getName());
                    nodeSet.setDisplayName(Bundle.CLT_AggregationProcess_Name());
                    nodeSet.setShortDescription(Bundle.CLT_AggregationProcess_Description());
                    sheet.put(nodeSet);

                    if (aggregatorDescriptor.getAggregationStrategy() != null) {
                        ClassProperty classProperty = new AggregationStrategyInstanceProperty(aggregator, aggregatorDescriptor);
                        nodeSet.put(classProperty);

                    }
                    if (aggregatorDescriptor.getTileCachingStrategy() != null) {
                        ClassProperty classProperty = new CachingStrategyProperty(aggregator, aggregatorDescriptor);
                        nodeSet.put(classProperty);
                    }

                }

                if (aggregator.getAggContainer().getAggregationStrategy() instanceof AggregationStrategy) {
                    AggregationStrategy aggregationStrategy = (AggregationStrategy) aggregator.getAggContainer().getAggregationStrategy();
                    PropertySet[] propertySets = aggregationStrategy.getNodeDelegate().getPropertySets();
                    for (PropertySet propertySet : propertySets) {
                        Sheet.Set set = convertToSet(propertySet);
                        sheet.put(set);
                    }
                }

                if (aggregator.getAggContainer().getCachingStrategy() instanceof CachingStrategy) {
                    CachingStrategy cachingStrategy = (CachingStrategy) aggregator.getAggContainer().getCachingStrategy();
                    PropertySet[] propertySets = cachingStrategy.getNodeDelegate().getPropertySets();

                    for (PropertySet propertySet : propertySets) {
                        Sheet.Set set = convertToSet(propertySet);
                        sheet.put(set);
                    }
                }
            }
            return sheet;
        }

        private Sheet.Set convertToSet(PropertySet propertySet) {
            Sheet.Set set = Sheet.createPropertiesSet();
            set.setName(propertySet.getName());
            set.setDisplayName(propertySet.getDisplayName());
            set.setShortDescription(propertySet.getShortDescription());

            for (Property<?> property : propertySet.getProperties()) {
                set.put(property);
            }
            return set;
        }

        private class AggregationStrategyInstanceProperty extends ClassProperty {

            private final Aggregator aggregator;
            private final AggregatorDescriptor property;
            private ClassWrapper wrapper = null;

            public AggregationStrategyInstanceProperty(Aggregator aggregator, AggregatorDescriptor property) {
                super(AggregationStrategy.class.getName(),
                        Bundle.CLT_Property_Aggregator_Strategy_Class_Name(),
                        Bundle.CLT_Property_Aggregator_Strategy_Class_Description(),
                        AggregationStrategy.class);
                this.aggregator = aggregator;
                this.property = property;
                wrapper = aggregator.getAggContainer().getAggregationStrategy() != null
                        ? new ClassWrapper(aggregator.getAggContainer().getAggregationStrategy().getClass())
                        : null;
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                ClassWrapper oldValue = getValue();
                if (val == null) {
                    throw new IllegalArgumentException("null is not a valid value");
                } else if (!val.getQualifiedName().equals(wrapper.getQualifiedName())
                        && oldValue != null) {
                    wrapper = val;
                    ProcessDescriptor processDescriptor = aggregationProcess.getProcessDescriptor();
                    if (processDescriptor != null) {
                        List<PropertySection> sections = processDescriptor.getProperties().getSections();
                        int index = -1;
                        for (PropertySection propertySection : new ArrayList<PropertySection>(sections)) {
                            index++;
                            if (oldValue.getQualifiedName().equals(propertySection.getId())) {
                                LOG.info(MessageFormat.format("Strategy removed: {0}", sections.remove(propertySection)));
                            }
                        }

                        AggregationStrategy aggregationStrategy;
                        try {
                            aggregationStrategy = AggregationStrategy.Factory.find(val.getQualifiedName(), aggregator);
                            if (aggregationStrategy != null) {
                                aggregator.getAggContainer().setAggregationStrategy(aggregationStrategy);

                                if (index < 0 || index >= sections.size()) {
                                    sections.add(aggregationStrategy.getPropertySection());
                                } else {
                                    sections.add(index, aggregationStrategy.getPropertySection());
                                }
                            }
                            property.setAggregationStrategy(val.getQualifiedName());
                        } catch (DescriptorFactory.InstanceNotFountException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        }

        private class CachingStrategyProperty extends ClassProperty {

            private final AggregatorDescriptor property;
            private final Aggregator aggregator;
            private ClassWrapper wrapper;

            public CachingStrategyProperty(Aggregator aggregator, AggregatorDescriptor property) {
                super(CachingStrategy.class.getName(),
                        Bundle.CLT_Property_TileCache_Strategy_Class_Name(),
                        Bundle.CLT_Property_TileCache_Strategy_Class_Description(),
                        CachingStrategy.class);
                this.aggregator = aggregator;
                this.property = property;
                this.wrapper = aggregator.getAggContainer().getCachingStrategy() != null
                        ? new ClassWrapper(aggregator.getAggContainer().getCachingStrategy().getClass())
                        : null;
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    throw new IllegalArgumentException("null not a valid value");
                } else if (!val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                    ClassWrapper oldValue = wrapper;
                    wrapper = val;
                    ProcessDescriptor processDescriptor = aggregationProcess.getProcessDescriptor();
                    if (processDescriptor != null) {
                        List<PropertySection> sections = processDescriptor.getProperties().getSections();
                        int index = -1;
                        for (PropertySection propertySection : new ArrayList<PropertySection>(sections)) {
                            index++;
                            if (oldValue.getQualifiedName().equals(propertySection.getId())) {
                                LOG.info(MessageFormat.format("Caching strategy removed: {0}", sections.remove(propertySection)));
                            }
                        }

                        CachingStrategy cachingStrategy;
                        try {
                            cachingStrategy = CachingStrategy.Factory.find(val.getQualifiedName(), aggregator);
                            if (cachingStrategy != null) {
                                aggregator.getAggContainer().setCachingStrategy(cachingStrategy);
                                if (index < 0 || index >= sections.size()) {
                                    sections.add(cachingStrategy.getPropertySection());
                                } else {
                                    sections.add(index, cachingStrategy.getPropertySection());
                                }
                            }
                            property.setTileCachingStrategy(val.getQualifiedName());
                        } catch (DescriptorFactory.InstanceNotFountException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                    }

                }
            }
        }
    }
}
