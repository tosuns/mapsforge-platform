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
import de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.MergeHandler;
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
    "IterativeClosestPointsMerge_Name=Iterative Closest Points Merge Handler",
    "IterativeClosestPointsMerge_Description=The default merge handler implementation",
    "IterativeClosestPointsMerge_MaxPointGhostDistance_Name=Max Point Ghost Distance",
    "IterativeClosestPointsMerge_MaxPointGhostDistance_Description=No description available",
    "IterativeClosestPointsMerge_MaxLookahead_Name=Max Lookahead",
    "IterativeClosestPointsMerge_MaxLookahead_Description=No description available",
    "IterativeClosestPointsMerge_MinContinuationAngle_Name=Min Continuation Angle",
    "IterativeClosestPointsMerge_MinContinuationAngle_Description=No description available",
    "IterativeClosestPointsMerge_Setting_PropertySet_Name=Default Merge Handler Settings",
    "IterativeClosestPointsMerge_Setting_PropertySet_Description=Parameter to configure the merge handler instance."
})
@ServiceProvider(service = MergeHandler.class)
public class IterativeClosestPointsMerge extends de.fub.agg2graph.agg.strategy.IterativeClosestPointsMerge implements MergeHandler {

    private static final Logger LOG = Logger.getLogger(IterativeClosestPointsMerge.class.getName());
    private static final String PROP_NAME_MAX_POINT_GHOST_DISTANCE = "IterativeClosestPointsMerge.mergeHandler.max.point.ghost.distance";
    private static final String PROP_NAME_MAX_LOOKAHEAD = "IterativeClosestPointsMerge.mergeHandler.max.lookahead";
    private static final String PROP_NAME_MIN_CONTINUATION_ANGLE = "IterativeClosestPointsMerge.min.continuation.angle";
    private Aggregator aggregator;
    private PropertySet propertySet = null;
    private MergeHandlerNode nodeDelegate;

    private void reInit() {
        propertySet = null;
        propertySet = getPropertySet();
        if (propertySet != null) {
            for (Property property : propertySet.getProperties()) {
                if (property.getValue() != null) {
                    try {
                        if (PROP_NAME_MAX_LOOKAHEAD.equals(property.getId())) {
                            setMaxLookahead(Integer.parseInt(property.getValue()));
                        } else if (PROP_NAME_MAX_POINT_GHOST_DISTANCE.equals(property.getId())) {
                            setMaxPointGhostDist(Double.parseDouble(property.getValue()));
                        } else if (PROP_NAME_MIN_CONTINUATION_ANGLE.equals(property.getId())) {
                            setMinContinuationAngle(Double.parseDouble(property.getValue()));
                        }
                    } catch (NumberFormatException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    @Override
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        reInit();
    }

    @Override
    public Aggregator getAggregator() {
        return this.aggregator;
    }

    @Override
    public PropertySet getPropertySet() {
        if (propertySet == null) {
            if (getAggregator() != null) {
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    // look for the AggregationProcess descriptor
                    if (descriptor != null
                            && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        List<PropertySection> sections = descriptor.getProperties().getSections();
                        for (PropertySection propertySection : sections) {
                            for (PropertySet set : propertySection.getPropertySet()) {
                                // look for the propertySet with the same name as this class
                                if (IterativeClosestPointsMerge.class.getName().equals(set.getId())) {
                                    propertySet = set;
                                    break;
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
                Bundle.IterativeClosestPointsMerge_Setting_PropertySet_Name(),
                Bundle.IterativeClosestPointsMerge_Setting_PropertySet_Description());
        set.setId(DefaultMergeHandler.class.getName());

        Property property = new Property();
        property.setId(PROP_NAME_MAX_LOOKAHEAD);
        property.setJavaType(Integer.class.getName());
        property.setName(Bundle.IterativeClosestPointsMerge_MaxLookahead_Name());
        property.setDescription(Bundle.IterativeClosestPointsMerge_MaxLookahead_Description());
        property.setValue(MessageFormat.format("{0}", getMaxLookahead()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAX_POINT_GHOST_DISTANCE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.IterativeClosestPointsMerge_MaxPointGhostDistance_Name());
        property.setDescription(Bundle.IterativeClosestPointsMerge_MaxPointGhostDistance_Description());
        property.setValue(MessageFormat.format("{0}", getMaxPointGhostDist()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MIN_CONTINUATION_ANGLE);
        property.setJavaType(Double.class.getName());
        property.setName(Bundle.IterativeClosestPointsMerge_MinContinuationAngle_Name());
        property.setDescription(Bundle.IterativeClosestPointsMerge_MinContinuationAngle_Description());
        property.setValue(MessageFormat.format("{0}", getMinContinuationAngle()).replaceAll("\\,", "."));
        set.getProperties().add(property);

        return set;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new MergeHandlerNode(IterativeClosestPointsMerge.this);
        }
        return nodeDelegate;
    }

    private static class MergeHandlerNode extends AbstractNode {

        private final IterativeClosestPointsMerge mergeHandler;

        public MergeHandlerNode(IterativeClosestPointsMerge mergeHandler) {
            super(Children.LEAF);
            this.mergeHandler = mergeHandler;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Node.PropertySet[] propertySets = sheet.toArray();

            for (Node.PropertySet set : propertySets) {
                sheet.remove(set.getName());
            }

            if (mergeHandler != null) {
                de.fub.maps.project.aggregator.xml.PropertySet propertySet = mergeHandler.getPropertySet();

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
