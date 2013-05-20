/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.roadgeneration;

import de.fub.mapsforge.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.RoadObjectMerger;
import de.fub.mapsforge.project.aggregator.xml.AggregatorDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Properties;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.utilsmodule.node.property.NodeProperty;
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
    "CLT_DefaultRoadObjectMerger_Name=Road Object Merger",
    "CLT_DefaultRoadObjectMerger_Description=No description available",
    "CLT_DefaultRoadObjectMerger_Property_MinIntersectionMergeDistance_Name=Min Intersection Merge Distance",
    "CLT_DefaultRoadObjectMerger_Property_MinIntersectionMergeDistance_Description=No description available",
    "CLT_DefaultRoadObjectMerger_Property_MaxRoadMergeDistance_Name=Max Road Merge Distance",
    "CLT_DefaultRoadObjectMerger_Property_MaxRoadMergeDistance_Description=No description available"
})
@ServiceProvider(service = RoadObjectMerger.class)
public class DefaultRoadObjectMerger extends de.fub.agg2graph.roadgen.DefaultRoadObjectMerger implements RoadObjectMerger {

    private static final Logger LOG = Logger.getLogger(DefaultRoadObjectMerger.class.getName());
    private static final String PROP_NAME_MAX_INTERSECTION_MERGE_DISTANCE = "default.road.object.merger.prop.maxIntersectionMergeDistance";
    private static final String PROP_NAME_MAX_ROAD_MERGE_DISTANCE = "default.road.object.merger.prop.maxRoadMergeDistance";
    private RoadNetworkProcess roadNetworkProcess;
    private PropertySet propertySet;
    private RoadObjectMergerNode nodeDelegate;

    private void reInit() {
        propertySet = null;
        propertySet = getProcessDescriptor();
        if (propertySet != null) {
            List<Property> properties = propertySet.getProperties();
            for (Property property : properties) {
                if (property.getValue() != null) {
                    try {
                        if (PROP_NAME_MAX_ROAD_MERGE_DISTANCE.equals(property.getId())) {
                            setMaxIntersectionMergeDistance(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MAX_INTERSECTION_MERGE_DISTANCE.equals(property.getId())) {
                            setMaxRoadMergeDistance(Double.parseDouble(property.getValue()));
                        }
                    } catch (Throwable e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        }
    }

    @Override
    public void setRoadNetworkProcess(RoadNetworkProcess roadNetworkProcess) {
        this.roadNetworkProcess = roadNetworkProcess;
        reInit();
    }

    @Override
    public RoadNetworkProcess getRoadNetworkProcess() {
        return this.roadNetworkProcess;
    }

    @Override
    public PropertySet getProcessDescriptor() {
        if (propertySet == null) {
            if (getRoadNetworkProcess() != null && getRoadNetworkProcess().getAggregator() != null) {
                AggregatorDescriptor aggregatorDescriptor = getRoadNetworkProcess().getAggregator().getAggregatorDescriptor();
                List<ProcessDescriptor> list = aggregatorDescriptor.getPipeline().getList();

                OUTERLOOP:
                for (ProcessDescriptor descriptor : list) {
                    if (descriptor != null
                            && RoadNetworkProcess.class.getName().equals(descriptor.getJavaType())) {
                        Properties properties = descriptor.getProperties();
                        for (PropertySection section : properties.getSections()) {
                            for (PropertySet set : section.getPropertySet()) {
                                if (getClass().getName().equals(set.getId())) {
                                    propertySet = set;
                                    break OUTERLOOP;
                                }
                            }
                        }
                        break;
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
        PropertySet set = new PropertySet();
        set.setId(DefaultRoadObjectMerger.class.getName());
        set.setName(Bundle.CLT_DefaultRoadObjectMerger_Name());
        set.setDescription(Bundle.CLT_DefaultRoadObjectMerger_Description());

        Property property = new Property();
        property.setId(PROP_NAME_MAX_INTERSECTION_MERGE_DISTANCE);
        property.setJavaType(Double.class.getName());
        property.setValue("30");
        property.setName(Bundle.CLT_DefaultRoadObjectMerger_Property_MinIntersectionMergeDistance_Name());
        property.setDescription(Bundle.CLT_DefaultRoadObjectMerger_Property_MinIntersectionMergeDistance_Description());
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_ROAD_MERGE_DISTANCE);
        property.setJavaType(Double.class.getName());
        property.setValue("50");
        property.setName(Bundle.CLT_DefaultRoadObjectMerger_Property_MaxRoadMergeDistance_Name());
        property.setDescription(Bundle.CLT_DefaultRoadObjectMerger_Property_MaxRoadMergeDistance_Description());
        set.getProperties().add(property);

        return set;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new RoadObjectMergerNode(DefaultRoadObjectMerger.this);
        }
        return nodeDelegate;
    }

    private static class RoadObjectMergerNode extends AbstractNode {

        private final DefaultRoadObjectMerger objectMerger;

        public RoadObjectMergerNode(DefaultRoadObjectMerger objectMerger) {
            super(Children.LEAF);
            this.objectMerger = objectMerger;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            if (this.objectMerger != null) {
                sheet = Sheet.createDefault();
                de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet = this.objectMerger.getProcessDescriptor();
                if (propertySet != null) {
                    Sheet.Set set = Sheet.createPropertiesSet();
                    set.setName(propertySet.getId());
                    set.setDisplayName(propertySet.getName());
                    set.setShortDescription(propertySet.getDescription());
                    sheet.put(set);

                    for (de.fub.mapsforge.project.aggregator.xml.Property prop : propertySet.getProperties()) {
                        set.put(new NodeProperty(prop));
                    }
                }
            }
            return sheet;
        }
    }
}
