/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.roadgeneration;

import de.fub.maps.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.RoadNetworkFilter;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.Property;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.aggregator.xml.PropertySet;
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
    "CLT_DefaultRoadNetworkFilter_Name=Default Road Network Filter",
    "CLT_DefaultRoadNetworkFilter_Description=No description available",
    "CLT_DefaultRoadNetworkFilter_Property_RemoveBorderRoads_Name=Remove Border Roads",
    "CLT_DefaultRoadNetworkFilter_Property_RemoveBorderRoads_Description=No description available",
    "CLT_DefaultRoadNetworkFilter_Property_MinBorderRoadLength_Name=Min Border Road Length",
    "CLT_DefaultRoadNetworkFilter_Property_MinDordarRoadLength_Description=No description available",
    "CLT_DefaultRoadNetworkFilter_Property_RemoveIsolatedRoads_Name=Remove Isolated Roads",
    "CLT_DefaultRoadNetworkFilter_Property_RemoveIsolatedRoads_Description=No description available",
    "CLT_DefaultRoadNetworkFilter_Property_MinIsolatedRoadLength_Name=Min Isolated Road Length",
    "CLT_DefaultRoadNetworkFilter_Property_MinIsolatedRoadLength_Description=No description available"
})
@ServiceProvider(service = RoadNetworkFilter.class)
public class DefaultRoadNetworkFilter extends de.fub.agg2graph.roadgen.DefaultRoadNetworkFilter implements RoadNetworkFilter {

    private static final Logger LOG = Logger.getLogger(DefaultRoadNetworkFilter.class.getName());
    private static final String PROP_NAME_REMOVE_BORDER_ROADS = "default.road.Network.filter.prop.removeBorderRoads";
    private static final String PROP_NAME_MIN_BORDER_ROAD_LENGTH = "default.road.network.filter.prop.minBorderRoadLength";
    private static final String PROP_NAME_REMOVE_ISOLATED_ROADS = "default.road.network.filter.prop.removeIsolatedRoads";
    private static final String PROP_NAME_MIN_ISOLATED_ROAD_LENGTH = "default.read.network.filter.prop.minIsolatedRoadLength";
    private RoadNetworkProcess roadNetworkProcess;
    private PropertySet propertySet;
    private RoadNetworkFilterNode nodeDelegate;

    private void reInit() {
        propertySet = null;
        propertySet = getProcessDescriptor();
        if (propertySet != null) {
            List<Property> properties = propertySet.getProperties();
            for (Property property : properties) {
                if (property.getValue() != null) {
                    try {
                        if (PROP_NAME_MIN_BORDER_ROAD_LENGTH.equals(property.getId())) {
                            setMinBorderRoadLength(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MIN_ISOLATED_ROAD_LENGTH.equals(property.getId())) {
                            setMinBorderRoadLength(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_REMOVE_BORDER_ROADS.equals(property.getId())) {
                            setRemoveBorderRoads(Boolean.parseBoolean(property.getValue()));
                        } else if (PROP_NAME_REMOVE_ISOLATED_ROADS.equals(property.getId())) {
                            setRemoveIsolatedRoads(Boolean.parseBoolean(property.getValue()));
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
                            && getClass().getName().equals(descriptor.getJavaType())) {
                        for (PropertySection section : descriptor.getProperties().getSections()) {
                            for (PropertySet set : section.getPropertySet()) {
                                if (DefaultRoadNetworkFilter.class.getName().equals(set.getId())) {
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
        set.setId(DefaultRoadNetworkFilter.class.getName());
        set.setName(Bundle.CLT_DefaultRoadNetworkFilter_Name());
        set.setDescription(Bundle.CLT_DefaultRoadNetworkFilter_Description());

        Property property = new Property();
        property.setId(PROP_NAME_REMOVE_BORDER_ROADS);
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        property.setName(Bundle.CLT_DefaultRoadNetworkFilter_Property_RemoveBorderRoads_Name());
        property.setDescription(Bundle.CLT_DefaultRoadNetworkFilter_Property_RemoveBorderRoads_Description());
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_BORDER_ROAD_LENGTH);
        property.setJavaType(Double.class.getName());
        property.setValue("150");
        property.setName(Bundle.CLT_DefaultRoadNetworkFilter_Property_MinBorderRoadLength_Name());
        property.setDescription(Bundle.CLT_DefaultRoadNetworkFilter_Property_MinDordarRoadLength_Description());
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_REMOVE_ISOLATED_ROADS);
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        property.setName(Bundle.CLT_DefaultRoadNetworkFilter_Property_RemoveIsolatedRoads_Name());
        property.setDescription(Bundle.CLT_DefaultRoadNetworkFilter_Property_RemoveIsolatedRoads_Description());
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_ISOLATED_ROAD_LENGTH);
        property.setJavaType(Double.class.getName());
        property.setValue("500");
        property.setName(Bundle.CLT_DefaultRoadNetworkFilter_Property_MinIsolatedRoadLength_Name());
        property.setDescription(Bundle.CLT_DefaultRoadNetworkFilter_Property_MinIsolatedRoadLength_Description());
        set.getProperties().add(property);

        return set;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new RoadNetworkFilterNode(DefaultRoadNetworkFilter.this);
        }
        return nodeDelegate;
    }

    private static class RoadNetworkFilterNode extends AbstractNode {

        private final DefaultRoadNetworkFilter filter;

        public RoadNetworkFilterNode(DefaultRoadNetworkFilter filter) {
            super(Children.LEAF);
            this.filter = filter;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            if (filter != null) {
                sheet = Sheet.createDefault();

                de.fub.maps.project.aggregator.xml.PropertySet propertySet = filter.getProcessDescriptor();
                if (propertySet != null) {
                    Sheet.Set set = Sheet.createPropertiesSet();
                    set.setName(propertySet.getId());
                    set.setDisplayName(propertySet.getName());
                    set.setShortDescription(propertySet.getDescription());
                    sheet.put(set);

                    for (de.fub.maps.project.aggregator.xml.Property prop : propertySet.getProperties()) {
                        set.put(new NodeProperty(prop));
                    }
                }
            }
            return sheet;
        }
    }
}
