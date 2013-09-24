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

import de.fub.maps.project.aggregator.pipeline.processes.AggregationProcess;
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.TraceDistance;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.Property;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.aggregator.xml.PropertySet;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.node.property.NodeProperty;
import java.text.MessageFormat;
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
    "FreeSpaceMatch_Name=FreeSpace Trace Distance",
    "FreeSpaceMatch_Description=No Description available",
    "FreeSpaceMatch_MaxDistance_Name=Max Distance",
    "FreeSpaceMatch_MaxDistance_Description=No description available",
    "FreeSpaceMatch_MinLengthFirstSegment_Name=Min Length First Segment",
    "FreeSpaceMatch_MinLengthFirstSegment_Description=No description available",
    "FreeSpaceMatch_MaxAngle_Name=Max Angle",
    "FreeSpaceMatch_MaxAngle_Descriptionn=No description available",
    "FreeSpaceMatch_Settings_PropertySet_Name=FreeSpace Trace Distance Settings",
    "FreeSpaceMatch_Settings_PropertySet_Description=Parameters to configure this TraceDistance"
})
@ServiceProvider(service = TraceDistance.class)
public class FreeSpaceMatch extends de.fub.agg2graph.agg.strategy.FreeSpaceMatch implements TraceDistance {

    private static final Logger LOG = Logger.getLogger(FreeSpaceMatch.class.getName());
    private static final String PROP_NAME_MAX_DISTANCE = "FreeSpaceMatch.trace.distance.maxDistance";
    private static final String PROP_NAME_MIN_LENGTH_FIRST_SEGMENT = "FreeSpaceMatch.trace.distance.maxLengthFirstSegment";
    private static final String PROP_NAME_MAX_ANGLE = "FreeSpaceMatch.trace.distance.maxAngle";
    private Aggregator aggregator;
    private PropertySet propertySet;
    private TraceDistanceNode nodeDelegate;

    @Override
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        reInit();
    }

    private void reInit() {
        propertySet = null;
        propertySet = getPropertySet();
        if (propertySet != null) {

            List<Property> properties = propertySet.getProperties();
            for (Property property : properties) {
                try {
                    if (property.getValue() != null) {
                        if (PROP_NAME_MAX_ANGLE.equals(property.getId())) {
                            setMaxAngle(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MAX_DISTANCE.equals(property.getId())) {
                            setMaxDistance(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MIN_LENGTH_FIRST_SEGMENT.equals(property.getId())) {
                            setMinLengthFirstSegment(Integer.parseInt(property.getValue()));
                        }
                    }
                } catch (NumberFormatException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

        }
    }

    @Override
    public Aggregator getAggregator() {
        return this.aggregator;
    }

    @Override
    public PropertySet getPropertySet() {
        if (propertySet == null) {
            if (getAggregator() != null) {
                OUTERLOOP:
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (descriptor != null
                            && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        List<PropertySection> sections = descriptor.getProperties().getSections();
                        for (PropertySection section : sections) {
                            for (PropertySet set : section.getPropertySet()) {
                                if (FreeSpaceMatch.class.getName().equals(set.getId())) {
                                    propertySet = set;
                                    break OUTERLOOP;
                                }
                            }
                        }
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

        PropertySet set = new PropertySet(
                Bundle.FreeSpaceMatch_Settings_PropertySet_Name(),
                Bundle.FreeSpaceMatch_Settings_PropertySet_Description());
        set.setId(FreeSpaceMatch.class.getName());

        Property property = new Property();
        property.setId(PROP_NAME_MAX_ANGLE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.FreeSpaceMatch_MaxAngle_Name());
        property.setDescription(Bundle.FreeSpaceMatch_MaxAngle_Descriptionn());
        property.setValue(MessageFormat.format("{0}", getMaxAngle()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_DISTANCE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.FreeSpaceMatch_MaxDistance_Name());
        property.setDescription(Bundle.FreeSpaceMatch_MaxDistance_Description());
        property.setValue(MessageFormat.format("{0}", getMaxDistance()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_LENGTH_FIRST_SEGMENT);
        property.setJavaType(Integer.class.getName());
        property.setName(Bundle.FreeSpaceMatch_MinLengthFirstSegment_Name());
        property.setDescription(Bundle.FreeSpaceMatch_MinLengthFirstSegment_Name());
        property.setValue(MessageFormat.format("{0}", getMinLengthFirstSegment()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        return set;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new TraceDistanceNode(FreeSpaceMatch.this);
        }
        return nodeDelegate;
    }

    private static class TraceDistanceNode extends AbstractNode {

        private final FreeSpaceMatch traceDistance;

        public TraceDistanceNode(FreeSpaceMatch traceDistance) {
            super(Children.LEAF);
            this.traceDistance = traceDistance;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();

            if (this.traceDistance != null) {
                de.fub.maps.project.aggregator.xml.PropertySet propertySet = this.traceDistance.getPropertySet();
                if (propertySet != null) {
                    Sheet.Set set = Sheet.createPropertiesSet();
                    set.setName(propertySet.getId());
                    set.setDisplayName(propertySet.getName());
                    set.setShortDescription(propertySet.getDescription());
                    sheet.put(set);

                    for (de.fub.maps.project.aggregator.xml.Property property : propertySet.getProperties()) {
                        set.put(new NodeProperty(property));
                    }

                }
            }
            return sheet;
        }
    }
}
