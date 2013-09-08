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
package de.fub.maps.project.aggregator.pipeline.wrapper.roadgeneration;

import de.fub.agg2graph.roadgen.DefaultAggFilter;
import de.fub.maps.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.RoadAggregationFilter;
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
    "CLT_DefaultRoadAggregationFilter_Name=Default Road Aggregation Filter",
    "CLT_DefaultRoadAggregationFilter_Description=No description available",
    "CLT_DefaultRoadAggregationFilter_Property_MinEgdeWeight_Name=Min Edge Weight",
    "CLT_DefaultRoadAggregationFilter_Property_MinEdgeWeight_Description=No description available"
})
@ServiceProvider(service = RoadAggregationFilter.class)
public class DefaultRoadAggregationFilter extends DefaultAggFilter implements RoadAggregationFilter {

    private static final Logger LOG = Logger.getLogger(DefaultRoadAggregationFilter.class.getName());
    private static final String PROP_NAME_MIN_EDGE_WEIGHT = "default.road.agg.filter.prop.minedgeweight";
    private RoadNetworkProcess roadNetworkProcess;
    private PropertySet propertySet;
    private Node nodeDelegate;

    private void reInit() {
        propertySet = null;
        propertySet = getProcessDescriptor();
        if (propertySet != null) {
            List<Property> properties = propertySet.getProperties();
            for (Property property : properties) {
                if (property.getValue() != null) {
                    try {
                        if (PROP_NAME_MIN_EDGE_WEIGHT.equals(property.getId())) {
                            setMinEdgeWeight(Double.parseDouble(property.getValue()));
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
                for (ProcessDescriptor processDescriptor : list) {
                    if (processDescriptor != null && RoadNetworkProcess.class.getName().equals(processDescriptor.getJavaType())) {
                        for (PropertySection section : processDescriptor.getProperties().getSections()) {
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
        set.setId(DefaultRoadAggregationFilter.class.getName());
        set.setName(Bundle.CLT_DefaultRoadAggregationFilter_Name());
        set.setDescription(Bundle.CLT_DefaultRoadAggregationFilter_Description());

        Property property = new Property();
        property.setId(PROP_NAME_MIN_EDGE_WEIGHT);
        property.setJavaType(Double.class.getName());
        property.setValue("2");
        property.setName(Bundle.CLT_DefaultRoadAggregationFilter_Property_MinEgdeWeight_Name());
        property.setDescription(Bundle.CLT_DefaultRoadAggregationFilter_Property_MinEdgeWeight_Description());
        set.getProperties().add(property);

        return set;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new RoadAggregationFilterNode(DefaultRoadAggregationFilter.this);
        }
        return nodeDelegate;
    }

    private static class RoadAggregationFilterNode extends AbstractNode {

        private final DefaultRoadAggregationFilter filter;

        public RoadAggregationFilterNode(DefaultRoadAggregationFilter filter) {
            super(Children.LEAF);
            this.filter = filter;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();

            if (this.filter != null) {
                de.fub.maps.project.aggregator.xml.PropertySet propertySet = this.filter.getProcessDescriptor();
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
            } else {
                sheet = super.createSheet();
            }

            return sheet;
        }
    }
}
