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

import de.fub.maps.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.RoadTypeClassifier;
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
    "CLT_DefaultRoadTypeClassifier_Name=Default Road Type Classifier",
    "CLT_DefaultRoadTypeClassifier_Description=No description available",
    "CLT_DefaultRoadTypeClassifier_Property_MinWeightPrimary_Name=Min Weight Primary",
    "CLT_DefaultRoadTypeClassifier_Property_MinWeightPrimary_Description=No description available",
    "CLT_DefaultRoadTypeClassifier_Property_MinWidthPrimary_Name=Min Width Primary",
    "CLT_DefaultRoadTypeClassifier_Property_MinWidthPrimary_Description=No description available",
    "CLT_DefaultRoadTypeClassifier_Property_MinWeightSecondary_Name=Min Weight Secondary",
    "CLT_DefaultRoadTypeClassifier_Property_MinWeightSecondary_Description=No description available",
    "CLT_DefaultRoadTypeClassifier_Property_MinWidthSecondary_Name=Min Width Secondary",
    "CLT_DefaultRoadTypeClassifier_Property_MinWidthSecondary_Description=No description available"
})
@ServiceProvider(service = RoadTypeClassifier.class)
public class DefaultRoadTypeClassifier extends de.fub.agg2graph.roadgen.DefaultRoadTypeClassifier implements RoadTypeClassifier {

    private static final Logger LOG = Logger.getLogger(DefaultRoadTypeClassifier.class.getName());
    private static final String PROP_NAME_MIN_WEIGHT_PRIMARY = "default.road.type.classifier.prop.minweightprimary";
    private static final String PROP_NAME_MIN_WIDTH_PRIMARY = "default.road.type.classifier.prop.minwidthprimary";
    private static final String PROP_NAME_MIN_WEIGHT_SECONDARY = "default.road.type.classifier.prop.minweightsecondary";
    private static final String PROP_NAME_MIN_WIDTH_SECONDARY = "default.road.type.classifier.prop.minwidthsecondary";
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
                        if (PROP_NAME_MIN_WEIGHT_PRIMARY.equals(property.getId())) {
                            setMinWeightPrimary(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MIN_WIDTH_PRIMARY.equals(property.getId())) {
                            setMinWidthPrimary(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MIN_WEIGHT_SECONDARY.equals(property.getId())) {
                            setMinWeightSecondary(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MIN_WIDTH_SECONDARY.equals(property.getId())) {
                            setMinWidthSecondary(Double.parseDouble(property.getValue()));
                        }
                    } catch (NumberFormatException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        }
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new RoadTypeClassifierNode(DefaultRoadTypeClassifier.this);
        }
        return nodeDelegate;
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
                List<ProcessDescriptor> processDescriptorList = aggregatorDescriptor.getPipeline().getList();

                OUTERLOOP:
                for (ProcessDescriptor processDescriptor : processDescriptorList) {
                    if (processDescriptor != null
                            && RoadNetworkProcess.class.getName().equals(processDescriptor.getJavaType())) {
                        List<PropertySection> sections = processDescriptor.getProperties().getSections();
                        for (PropertySection section : sections) {
                            for (PropertySet set : section.getPropertySet()) {
                                // convention the set id will always be the
                                // instance class name
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
            // check whether descriptor is still null
            // if so, initialize with the default descriptor
            if (propertySet == null) {
                propertySet = createDefaultPropertySet();
            }
        }
        return propertySet;
    }

    private PropertySet createDefaultPropertySet() {
        PropertySet set = new PropertySet();
        set.setId(DefaultRoadTypeClassifier.class.getName());
        set.setName(Bundle.CLT_DefaultRoadTypeClassifier_Name());
        set.setDescription(Bundle.CLT_DefaultRoadTypeClassifier_Description());

        Property property = new Property();
        property.setId(PROP_NAME_MIN_WEIGHT_PRIMARY);
        property.setJavaType(Double.class.getName());
        property.setValue("4");
        property.setName(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWeightPrimary_Name());
        property.setDescription(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWeightPrimary_Description());
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_WIDTH_PRIMARY);
        property.setJavaType(Double.class.getName());
        property.setValue("4");
        property.setName(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWidthPrimary_Name());
        property.setDescription(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWidthPrimary_Description());
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_WEIGHT_SECONDARY);
        property.setJavaType(Double.class.getName());
        property.setValue("2");
        property.setName(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWeightSecondary_Name());
        property.setDescription(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWeightSecondary_Description());
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_WIDTH_SECONDARY);
        property.setJavaType(Double.class.getName());
        property.setValue("2");
        property.setName(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWidthSecondary_Name());
        property.setDescription(Bundle.CLT_DefaultRoadTypeClassifier_Property_MinWidthSecondary_Description());
        set.getProperties().add(property);

        return set;
    }

    private static class RoadTypeClassifierNode extends AbstractNode {

        private final DefaultRoadTypeClassifier classifier;

        public RoadTypeClassifierNode(DefaultRoadTypeClassifier classifier) {
            super(Children.LEAF);
            this.classifier = classifier;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();

            if (classifier != null) {
                de.fub.maps.project.aggregator.xml.PropertySet propertySet = classifier.getProcessDescriptor();
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
