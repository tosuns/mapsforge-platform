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
    "GpxmergeTraceDistance_Name=Gpx Merge Trace Distance",
    "GpxmergeTraceDistance_Description=No description available",
    "GpxmergeTraceDistance_Settings_PropertySet_Name=Gpxmerge Trace Distance Settings",
    "GpxmergeTraceDistance_Settings_PropertySet_Description=No description available",
    "GpxmergeTraceDistance_AngleFactor_Name=Angle Factor",
    "GpxmergeTraceDistance_AngleFactor_Description=No description available"
})
@ServiceProvider(service = TraceDistance.class)
public class GpxmergeTraceDistance extends de.fub.agg2graph.agg.strategy.GpxmergeTraceDistance implements TraceDistance {

    private static final Logger LOG = Logger.getLogger(GpxmergeTraceDistance.class.getName());
    private static final String PROP_NAME_ANGLE_FACTOR = "gpx.merge.trace.distance.angle.factor";
    private Aggregator aggregator;
    private TraceDistanceNode nodeDelegate;
    private PropertySet propertySet;

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
                        if (PROP_NAME_ANGLE_FACTOR.equals(property.getId())) {
                            setAngleFactor(Double.parseDouble(property.getValue()));
                        }
                    }
                } catch (Exception ex) {
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
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new TraceDistanceNode(GpxmergeTraceDistance.this);
        }
        return nodeDelegate;
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
                                if (GpxmergeTraceDistance.class.getName().equals(set.getId())) {
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
                Bundle.GpxmergeTraceDistance_Name(),
                Bundle.GpxmergeTraceDistance_Description());
        set.setId(GpxmergeTraceDistance.class.getName());

        Property property = new Property();
        property.setId(PROP_NAME_ANGLE_FACTOR);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.GpxmergeTraceDistance_AngleFactor_Name());
        property.setDescription(Bundle.GpxmergeTraceDistance_AngleFactor_Description());
        property.setValue(MessageFormat.format("{0}", getAngleFactor()).replaceAll("\\,", "."));
        set.getProperties().add(property);
        return set;
    }

    private static class TraceDistanceNode extends AbstractNode {

        private final GpxmergeTraceDistance traceDistance;

        public TraceDistanceNode(GpxmergeTraceDistance traceDistance) {
            super(Children.LEAF);
            this.traceDistance = traceDistance;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            PropertySet[] propertySets = sheet.toArray();

            for (PropertySet set : propertySets) {
                sheet.remove(set.getName());
            }
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
