/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.processes;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.management.Statistics;
import de.fub.agg2graph.roadgen.IAggFilter;
import de.fub.agg2graph.roadgen.IRoadNetworkFilter;
import de.fub.agg2graph.roadgen.IRoadObjectMerger;
import de.fub.agg2graph.roadgen.IRoadTypeClassifier;
import de.fub.agg2graph.roadgen.Intersection;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graphui.layers.IntersectionLayer;
import de.fub.agg2graphui.layers.PrimaryRoadNetworkLayer;
import de.fub.agg2graphui.layers.RoadNetworkLayer;
import de.fub.agg2graphui.layers.SecondaryRoadNetworkLayer;
import de.fub.agg2graphui.layers.TertiaryRoadNetworkLayer;
import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassProperty;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassWrapper;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AggregationProcessNode;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.RoadAggregationFilter;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.RoadNetworkDescriptor;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.RoadNetworkFilter;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.RoadObjectMerger;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.RoadTypeClassifier;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import java.awt.Component;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_RoadNetworkProcess_Name=Road Generator",
    "CLT_RoadNetworkProcess_Description=Road Generation process",
    "CLT_RoadNetworkProcess_PropertySection_Name=Road Generator Settings",
    "CLT_RoadNetworkProcess_PropertySection_Description=No description available",
    "CLT_RoadNetworkProcess_Property_AggregationFilter_Name=Aggragation Filter Settings",
    "CLT_RoadNetworkProcess_Property_AggregationFilter_Description=No description available",
    "CLT_RoadNetworkProcess_Property_RoadObjectMerger_Name=Road Object Merger Settings",
    "CLT_RoadNetworkProcess_Property_RoadObjectMerger_Description=No description available",
    "CLT_RoadNetworkProcess_Property_Road Type_Classifier_Name=Road Type Classifier Settings",
    "CLT_RoadNetworkProcess_Property_Road_Type_Classifier_Description=No description available",
    "CLT_RoadNetworkProcess_Property_RoadNetworkFilter_Name=Road Network Filter Settings",
    "CLT_RoadNetworkProcess_Property_RoadNetworkFilter_Description=No description available",})
@ServiceProvider(service = AbstractAggregationProcess.class)
public class RoadNetworkProcess extends AbstractAggregationProcess<AggContainer, RoadNetwork> implements StatisticProvider {

    private static final Logger LOG = Logger.getLogger(RoadNetworkProcess.class.getName());
    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private static final String PROP_NAME_AGGREGATION_FILTER_INSTANCE = "road.network.process.aggregationFilter.instance";
    private static final String PROP_NAME_NETWORK_FILTER_INSTANCE = "road.network.process.network.filter.instance";
    private static final String PROP_NAME_OBJECT_MERGER_INSTANCE = "road.network.process.object.merger.instance";
    private static final String PROP_NAME_TYPE_CLASSIFIER_INSTANCE = "road.network.process.type.classifier.instance";
    private RoadNetwork roadNetwork = null;
    private IntersectionLayer intersectionLayer = new IntersectionLayer();
    private RoadNetworkLayer primaryRoadNetworkLayer = new PrimaryRoadNetworkLayer();
    private RoadNetworkLayer secondaryRoadNetworkLayer = new SecondaryRoadNetworkLayer();
    private RoadNetworkLayer teritaryRoadNetworkLayer = new TertiaryRoadNetworkLayer();
    private RoadObjectMerger roadObjectMerger = null;
    private RoadAggregationFilter roadAggregationFilter = null;
    private RoadNetworkFilter roadNetworkFilter = null;
    private RoadTypeClassifier roadTypeClassifier = null;
    private RoadNetwortProcessNode node;

    public RoadNetworkProcess() {
        init();
    }

    private void init() {
        getLayers().add(intersectionLayer);
        getLayers().add(primaryRoadNetworkLayer);
        getLayers().add(secondaryRoadNetworkLayer);
        getLayers().add(teritaryRoadNetworkLayer);
    }

    private void reInit() {
        node = null;
        if (getProcessDescriptor() != null) {
            List<PropertySection> sections = getProcessDescriptor().getProperties().getSections();
            for (PropertySection section : sections) {
                for (PropertySet propertySet : section.getPropertySet()) {
                    if (RoadNetworkProcess.class.getName().equals(propertySet.getId())) {
                        for (Property property : propertySet.getProperties()) {
                            if (PROP_NAME_AGGREGATION_FILTER_INSTANCE.equals(property.getId())) {
                                handleRoadAggregationFilterCreation(property);
                            } else if (PROP_NAME_NETWORK_FILTER_INSTANCE.equals(property.getId())) {
                                handleRoadNetworkFilterCreation(property);
                            } else if (PROP_NAME_OBJECT_MERGER_INSTANCE.equals(property.getId())) {
                                handleRoadObjectMergerCreation(property);
                            } else if (PROP_NAME_TYPE_CLASSIFIER_INSTANCE.equals(property.getId())) {
                                handleRoadTypeClassifierCreation(property);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    private void handleRoadObjectMergerCreation(Property property) {
        RoadObjectMerger merger = null;
        try {
            merger = RoadObjectMerger.Factory.find(property.getValue());
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            try {
                merger = RoadObjectMerger.Factory.getDefault();
            } catch (RoadNetworkDescriptor.InstanceNotFound ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
        if (merger != null) {
            merger.setRoadNetworkProcess(this);
            setRoadObjectMerger(merger);
        }
    }

    private void handleRoadAggregationFilterCreation(Property property) {
        RoadAggregationFilter filter = null;
        try {
            filter = RoadAggregationFilter.Factory.find(property.getValue());
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            try {
                filter = RoadAggregationFilter.Factory.getDefault();
            } catch (RoadNetworkDescriptor.InstanceNotFound ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
        if (filter != null) {
            filter.setRoadNetworkProcess(this);
            setRoadAggregationFilter(filter);
        }
    }

    private void handleRoadTypeClassifierCreation(Property property) {
        RoadTypeClassifier classifier = null;
        try {
            classifier = RoadTypeClassifier.Factory.find(property.getValue());
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            try {
                classifier = RoadTypeClassifier.Factory.getDefault();
            } catch (RoadNetworkDescriptor.InstanceNotFound ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
        if (classifier != null) {
            classifier.setRoadNetworkProcess(this);
            setRoadTypeClassifier(classifier);
        }
    }

    private void handleRoadNetworkFilterCreation(Property property) {
        RoadNetworkFilter filter = null;
        try {
            filter = RoadNetworkFilter.Factory.find(property.getValue());
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            try {
                filter = RoadNetworkFilter.Factory.getDefault();
            } catch (RoadNetworkDescriptor.InstanceNotFound ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
        if (filter != null) {
            filter.setRoadNetworkProcess(this);
            setRoadNetworkFilter(filter);
        }
    }

    public RoadNetwork getRoadNetwork() {
        return roadNetwork;
    }

    public void setRoadNetwork(RoadNetwork roadNetwork) {
        this.roadNetwork = roadNetwork;
    }

    public RoadObjectMerger getRoadObjectMerger() {
        return roadObjectMerger;
    }

    public void setRoadObjectMerger(RoadObjectMerger roadObjectMerger) {
        this.roadObjectMerger = roadObjectMerger;
    }

    public RoadAggregationFilter getRoadAggregationFilter() {
        return roadAggregationFilter;
    }

    public void setRoadAggregationFilter(RoadAggregationFilter roadAggregationFilter) {
        this.roadAggregationFilter = roadAggregationFilter;
    }

    public RoadNetworkFilter getRoadNetworkFilter() {
        return roadNetworkFilter;
    }

    public void setRoadNetworkFilter(RoadNetworkFilter roadNetworkFilter) {
        this.roadNetworkFilter = roadNetworkFilter;
    }

    public RoadTypeClassifier getRoadTypeClassifier() {
        return roadTypeClassifier;
    }

    public void setRoadTypeClassifier(RoadTypeClassifier roadTypeClassifier) {
        this.roadTypeClassifier = roadTypeClassifier;
    }

    @Override
    public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        super.setProcessDescriptor(processDescriptor);
        reInit();
    }

    @Override
    public void setInput(AggContainer input) {
        // we can access the aggContainer via the parent Aggregator instance.
    }

    @Override
    protected void start() {

        if (getAggregator() != null) {
            ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
            try {
                handle.start();
                primaryRoadNetworkLayer.clearRenderObjects();
                intersectionLayer.clearRenderObjects();

                roadNetwork = new RoadNetwork();
                roadNetwork.setAggFilter(getRoadAggregationFilter());
                roadNetwork.setRoadNetworkFilter(getRoadNetworkFilter());
                roadNetwork.setRoadObjectMerger(getRoadObjectMerger());
                roadNetwork.setRoadTypeClassifier(getRoadTypeClassifier());
                roadNetwork.parse(getAggregator().getAggContainer(), null);
                handle.switchToDeterminate(roadNetwork.getIntersections().size());
                int counter = 0;
                for (Intersection intersection : roadNetwork.getIntersections()) {

                    if (canceled.get()) {
                        fireProcessCanceledEvent();
                        return;
                    }

                    intersectionLayer.add(intersection);
                    handle.progress(++counter);
                }

                primaryRoadNetworkLayer.add(roadNetwork);
                secondaryRoadNetworkLayer.add(roadNetwork);
                teritaryRoadNetworkLayer.add(roadNetwork);
                fireProcessProgressEvent(new ProcessPipeline.ProcessEvent<RoadNetworkProcess>(this, "Creating Roadnetwork...", 100));
            } finally {
                handle.finish();
            }
        }
    }

    @Override
    public RoadNetwork getResult() {
        synchronized (RUN_MUTEX) {
            return roadNetwork;
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return Bundle.CLT_RoadNetworkProcess_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_RoadNetworkProcess_Description();
    }

    @Override
    public Image getIcon() {
        return IMAGE;
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @NbBundle.Messages({"CLT_No_Statistics_Available=No road network was computed to provide its statistics. Please run the read generator process!",
        "CLT_Description_Not_Available=Description not available."})
    @Override
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException {
        List<StatisticSection> statisticData = new ArrayList<StatisticSection>();

        // create process performance statistics
        StatisticSection section = getPerformanceData();
        statisticData.add(section);

        if (getResult() != null) {
            // create road network statistics
            section = new StatisticSection("Road Network Statistics", "Displays statistical data of the generated road network"); // NO18N
            statisticData.add(section);
            Map<String, Double> data = Statistics.getData(getResult());
            for (Entry<String, Double> entry : data.entrySet()) {
                section.getStatisticsItemList().add(new StatisticItem(entry.getKey(), String.valueOf(entry.getValue()), Bundle.CLT_Description_Not_Available()));
            }
        }
        return statisticData;
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

    @Override
    public Node getNodeDelegate() {
        if (node == null) {
            node = new RoadNetwortProcessNode(RoadNetworkProcess.this);
        }
        return node;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(RoadNetworkProcess.class.getName());
        descriptor.setDisplayName(Bundle.CLT_RoadNetworkProcess_Name());
        descriptor.setDescription(Bundle.CLT_RoadNetworkProcess_Description());

        PropertySection propertySection = new PropertySection();
        propertySection.setId(RoadNetworkProcess.class.getName());
        propertySection.setName(Bundle.CLT_RoadNetworkProcess_PropertySection_Name());
        propertySection.setDescription(Bundle.CLT_RoadNetworkProcess_PropertySection_Description());
        descriptor.getProperties().getSections().add(propertySection);

        PropertySet set = new PropertySet();
        set.setId(RoadNetworkProcess.class.getName());
        set.setName(Bundle.CLT_RoadNetworkProcess_PropertySection_Name());
        set.setDescription(Bundle.CLT_RoadNetworkProcess_PropertySection_Description());
        propertySection.getPropertySet().add(set);

        try {
            RoadAggregationFilter instance = RoadAggregationFilter.Factory.getDefault();
            PropertySet propertySet = instance.getProcessDescriptor();
            if (propertySet != null) {
                propertySection.getPropertySet().add(propertySet);
            }
            Property property = new Property();
            property.setId(PROP_NAME_AGGREGATION_FILTER_INSTANCE);
            property.setJavaType(String.class.getName());
            property.setValue(instance.getClass().getName());
            property.setName(Bundle.CLT_RoadNetworkProcess_Property_AggregationFilter_Name());
            property.setDescription(Bundle.CLT_RoadNetworkProcess_Property_AggregationFilter_Description());
            set.getProperties().add(property);
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            Exceptions.printStackTrace(ex);
        }

        try {
            RoadNetworkFilter instance = RoadNetworkFilter.Factory.getDefault();
            PropertySet propertySet = instance.getProcessDescriptor();
            if (propertySet != null) {
                propertySection.getPropertySet().add(propertySet);
            }
            Property property = new Property();
            property.setId(PROP_NAME_NETWORK_FILTER_INSTANCE);
            property.setJavaType(String.class.getName());
            property.setValue(instance.getClass().getName());
            property.setName(Bundle.CLT_RoadNetworkProcess_Property_RoadNetworkFilter_Name());
            property.setDescription(Bundle.CLT_RoadNetworkProcess_Property_RoadNetworkFilter_Description());
            set.getProperties().add(property);
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            Exceptions.printStackTrace(ex);
        }

        try {
            RoadObjectMerger instance = RoadObjectMerger.Factory.getDefault();
            PropertySet propertySet = instance.getProcessDescriptor();
            if (propertySet != null) {
                propertySection.getPropertySet().add(propertySet);
            }
            Property property = new Property();
            property.setId(PROP_NAME_OBJECT_MERGER_INSTANCE);
            property.setJavaType(String.class.getName());
            property.setValue(instance.getClass().getName());
            property.setName(Bundle.CLT_RoadNetworkProcess_Property_RoadObjectMerger_Name());
            property.setDescription(Bundle.CLT_RoadNetworkProcess_Property_RoadObjectMerger_Description());
            set.getProperties().add(property);
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            Exceptions.printStackTrace(ex);
        }

        try {
            RoadTypeClassifier instance = RoadTypeClassifier.Factory.getDefault();
            PropertySet propertySet = instance.getProcessDescriptor();
            if (propertySet != null) {
                propertySection.getPropertySet().add(propertySet);
            }
            Property property = new Property();
            property.setId(PROP_NAME_TYPE_CLASSIFIER_INSTANCE);
            property.setJavaType(String.class.getName());
            property.setValue(instance.getClass().getName());
            property.setName(Bundle.CLT_RoadNetworkProcess_Property_Road_Type_Classifier_Name());
            property.setDescription(Bundle.CLT_RoadNetworkProcess_Property_Road_Type_Classifier_Description());
            set.getProperties().add(property);
        } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
            Exceptions.printStackTrace(ex);
        }
        return descriptor;
    }

    private static class RoadNetwortProcessNode extends AggregationProcessNode {

        private final RoadNetworkProcess roadNetworkProcess;

        public RoadNetwortProcessNode(RoadNetworkProcess process) {
            super(process);
            this.roadNetworkProcess = process;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = null;

            if (roadNetworkProcess != null) {
                sheet = Sheet.createDefault();

                ProcessDescriptor processDescriptor = roadNetworkProcess.getProcessDescriptor();

                if (processDescriptor != null) {
                    List<PropertySection> sections = processDescriptor.getProperties().getSections();
                    for (final PropertySection section : sections) {
                        if (RoadNetworkProcess.class.getName().equals(section.getId())) {
                            for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                                if (RoadNetworkProcess.class.getName().equals(propertySet.getId())) {
                                    Sheet.Set set = Sheet.createPropertiesSet();
                                    set.setName(propertySet.getId());
                                    set.setDisplayName(propertySet.getName());
                                    set.setShortDescription(propertySet.getDescription());
                                    sheet.put(set);

                                    for (final de.fub.mapsforge.project.aggregator.xml.Property prop : propertySet.getProperties()) {
                                        if (PROP_NAME_AGGREGATION_FILTER_INSTANCE.equals(prop.getId())) {
                                            ClassProperty classProperty = new RoadAggregationFilterProperty(section, prop);
                                            set.put(classProperty);
                                        } else if (PROP_NAME_NETWORK_FILTER_INSTANCE.equals(prop.getId())) {
                                            ClassProperty classProperty = new RoadNetworkFilterProperty(section, prop);
                                            set.put(classProperty);
                                        } else if (PROP_NAME_OBJECT_MERGER_INSTANCE.equals(prop.getId())) {
                                            ClassProperty property = new RoadObjectMergerProperty(section, prop);
                                            set.put(property);
                                        } else if (PROP_NAME_TYPE_CLASSIFIER_INSTANCE.equals(prop.getId())) {
                                            ClassProperty property = new RoadTypeClassifierProperty(section, prop);
                                            set.put(property);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                IAggFilter aggFilter = roadNetworkProcess.getRoadAggregationFilter();
                if (aggFilter instanceof RoadNetworkDescriptor) {
                    RoadNetworkDescriptor descriptor = (RoadNetworkDescriptor) aggFilter;
                    PropertySet[] propertySets = descriptor.getNodeDelegate().getPropertySets();

                    for (PropertySet propertySet : propertySets) {
                        Sheet.Set set = convertToSet(propertySet);
                        sheet.put(set);
                    }

                }

                IRoadNetworkFilter roadNetworkFilter = roadNetworkProcess.getRoadNetworkFilter();
                if (roadNetworkFilter instanceof RoadNetworkDescriptor) {
                    RoadNetworkDescriptor descriptor = (RoadNetworkDescriptor) roadNetworkFilter;
                    PropertySet[] propertySets = descriptor.getNodeDelegate().getPropertySets();

                    for (PropertySet propertySet : propertySets) {
                        Sheet.Set set = convertToSet(propertySet);
                        sheet.put(set);
                    }

                }

                IRoadObjectMerger roadObjectMerger = roadNetworkProcess.getRoadObjectMerger();
                if (roadObjectMerger instanceof RoadNetworkDescriptor) {
                    RoadNetworkDescriptor descriptor = (RoadNetworkDescriptor) roadObjectMerger;
                    PropertySet[] propertySets = descriptor.getNodeDelegate().getPropertySets();

                    for (PropertySet propertySet : propertySets) {
                        Sheet.Set set = convertToSet(propertySet);
                        sheet.put(set);
                    }

                }
                IRoadTypeClassifier roadTypeClassifier = roadNetworkProcess.getRoadTypeClassifier();
                if (roadTypeClassifier instanceof RoadNetworkDescriptor) {
                    RoadNetworkDescriptor descriptor = (RoadNetworkDescriptor) roadTypeClassifier;
                    PropertySet[] propertySets = descriptor.getNodeDelegate().getPropertySets();

                    for (PropertySet propertySet : propertySets) {
                        Sheet.Set set = convertToSet(propertySet);
                        sheet.put(set);
                    }

                }
            }

            if (sheet == null) {
                sheet = super.createSheet();
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

        private class RoadAggregationFilterProperty extends ClassProperty {

            private final PropertySection section;
            private final de.fub.mapsforge.project.aggregator.xml.Property prop;
            private ClassWrapper wrapper = roadNetworkProcess.getRoadAggregationFilter() != null
                    ? new ClassWrapper(roadNetworkProcess.getRoadAggregationFilter().getClass())
                    : null;

            public RoadAggregationFilterProperty(PropertySection section, de.fub.mapsforge.project.aggregator.xml.Property prop) {
                super(prop.getId(), prop.getName(), prop.getDescription(), RoadAggregationFilter.class);
                this.section = section;
                this.prop = prop;
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    throw new IllegalArgumentException("Null is not a valid value");
                } else if (wrapper == null || !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                    ClassWrapper OldValue = this.wrapper;
                    wrapper = val;

                    int index = -1;
                    if (OldValue != null) {
                        for (de.fub.mapsforge.project.aggregator.xml.PropertySet set : section.getPropertySet()) {
                            index++;
                            if (OldValue.getQualifiedName().equals(set.getId())) {
                                LOG.info(MessageFormat.format("PropertySet {0} removed: {1}", set.getId(), section.getPropertySet().remove(set)));
                            }
                        }
                    }
                    try {

                        RoadAggregationFilter filter = RoadAggregationFilter.Factory.find(val.getQualifiedName());
                        if (filter != null) {
                            prop.setValue(val.getQualifiedName());
                            filter.setRoadNetworkProcess(roadNetworkProcess);
                            roadNetworkProcess.setRoadAggregationFilter(filter);
                            de.fub.mapsforge.project.aggregator.xml.PropertySet set = filter.getProcessDescriptor();
                            if (index < 0 || index >= section.getPropertySet().size()) {
                                section.getPropertySet().add(set);
                            } else {
                                section.getPropertySet().add(index, set);
                            }
                        }
                    } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        private class RoadNetworkFilterProperty extends ClassProperty {

            private final PropertySection section;
            private final de.fub.mapsforge.project.aggregator.xml.Property property;
            private ClassWrapper wrapper = roadNetworkProcess.getRoadNetworkFilter() != null
                    ? new ClassWrapper(roadNetworkProcess.getRoadNetworkFilter().getClass())
                    : null;

            public RoadNetworkFilterProperty(PropertySection section, de.fub.mapsforge.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), RoadNetworkFilter.class);
                this.section = section;
                this.property = property;
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    throw new IllegalArgumentException("null is not a valid value");
                } else if (wrapper == null || !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                    ClassWrapper oldValue = this.wrapper;
                    this.wrapper = val;
                    int index = -1;

                    if (oldValue != null) {
                        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                            index++;
                            if (oldValue.getQualifiedName().equals(propertySet.getId())) {
                                LOG.info(MessageFormat.format("PropertySet {0} removed: {1}", propertySet.getId(), section.getPropertySet().remove(propertySet)));
                            }
                        }
                    }
                    try {
                        RoadNetworkFilter filter = RoadNetworkFilter.Factory.find(val.getQualifiedName());
                        if (filter != null) {
                            property.setValue(val.getQualifiedName());
                            filter.setRoadNetworkProcess(roadNetworkProcess);
                            roadNetworkProcess.setRoadNetworkFilter(filter);
                            de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet = filter.getProcessDescriptor();
                            if (index < 0 || index >= section.getPropertySet().size()) {
                                section.getPropertySet().add(propertySet);
                            } else {
                                section.getPropertySet().add(propertySet);
                            }
                        }
                    } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        private class RoadObjectMergerProperty extends ClassProperty {

            private final PropertySection section;
            private final de.fub.mapsforge.project.aggregator.xml.Property property;
            private ClassWrapper wrapper = roadNetworkProcess.getRoadObjectMerger() != null
                    ? new ClassWrapper(roadNetworkProcess.getRoadObjectMerger().getClass())
                    : null;

            public RoadObjectMergerProperty(PropertySection section, de.fub.mapsforge.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), RoadObjectMerger.class);
                this.section = section;
                this.property = property;
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return this.wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    throw new IllegalArgumentException("Null is not a valid value");
                } else if (wrapper == null || !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                    ClassWrapper oldValue = this.wrapper;
                    this.wrapper = val;
                    int index = -1;

                    if (oldValue != null) {
                        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                            index++;
                            if (oldValue.getQualifiedName().equals(propertySet.getId())) {
                                LOG.info(MessageFormat.format("PropertySet {0} removed: {1}", propertySet.getId(), section.getPropertySet().remove(propertySet)));
                            }
                        }
                    }
                    try {
                        RoadObjectMerger roadObjectMerger = RoadObjectMerger.Factory.find(val.getQualifiedName());
                        if (roadObjectMerger != null) {
                            property.setValue(val.getQualifiedName());
                            roadObjectMerger.setRoadNetworkProcess(roadNetworkProcess);
                            roadNetworkProcess.setRoadObjectMerger(roadObjectMerger);
                            de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet = roadObjectMerger.getProcessDescriptor();
                            if (index < 0 || index >= section.getPropertySet().size()) {
                                section.getPropertySet().add(index, propertySet);
                            } else {
                                section.getPropertySet().add(propertySet);
                            }
                        }

                    } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
                        Exceptions.printStackTrace(ex);
                    }

                }
            }
        }

        private class RoadTypeClassifierProperty extends ClassProperty {

            private final PropertySection section;
            private final de.fub.mapsforge.project.aggregator.xml.Property property;
            private ClassWrapper wrapper = roadNetworkProcess.getRoadTypeClassifier() != null
                    ? new ClassWrapper(roadNetworkProcess.getRoadTypeClassifier().getClass())
                    : null;

            public RoadTypeClassifierProperty(PropertySection section, de.fub.mapsforge.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), RoadTypeClassifier.class);
                this.section = section;
                this.property = property;
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return this.wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    throw new IllegalArgumentException("null is not a valid value");
                } else if (wrapper == null || !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                    ClassWrapper oldValue = this.wrapper;
                    this.wrapper = val;
                    int index = -1;

                    if (oldValue != null) {
                        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                            index++;
                            if (oldValue.getQualifiedName().equals(propertySet.getId())) {
                                LOG.info(MessageFormat.format(" PropertySet {0} removed: {1}", propertySet.getId(), section.getPropertySet().remove(propertySet)));
                            }
                        }
                    }
                    try {
                        RoadTypeClassifier roadTypeClassifier = RoadTypeClassifier.Factory.find(val.getQualifiedName());
                        if (roadTypeClassifier != null) {
                            property.setValue(val.getQualifiedName());
                            roadTypeClassifier.setRoadNetworkProcess(roadNetworkProcess);
                            roadNetworkProcess.setRoadTypeClassifier(roadTypeClassifier);
                            de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet = roadTypeClassifier.getProcessDescriptor();
                            if (index < 0 || index >= section.getPropertySet().size()) {
                                section.getPropertySet().add(index, propertySet);
                            } else {
                                section.getPropertySet().add(propertySet);
                            }

                        }
                    } catch (RoadNetworkDescriptor.InstanceNotFound ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

            }
        }
    }
}
