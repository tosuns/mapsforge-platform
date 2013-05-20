/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper;

import de.fub.mapsforge.project.aggregator.pipeline.processes.AggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces.CachingStrategy;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
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
    "CLT_DefaultCachingStrategy_Name=Default Caching Strategy",
    "CLT_DefaultCachingStrategy_Description=Default caching strategy implementation.",
    "CLT_DefaultCachingStrategy_Property_MaxElementPerTile_Name=Max Element Per Tile",
    "CLT_DefaultCachingStrategy_Property_MaxElementPerTile_Description=No description available",
    "CLT_DefaultCachingStrategy_Property_SplitFactor_Name=Splitfactor",
    "CLT_DefaultCachingStrategy_Property_Splitfactor_Description=No description available"
})
@ServiceProvider(service = CachingStrategy.class)
public class DefaultCachingStrategy extends de.fub.agg2graph.agg.tiling.DefaultCachingStrategy implements CachingStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultCachingStrategy.class.getName());
    private static final String PROP_NAME_MAX_ELEMENT_COUNT = "default.caching.strategy.max.element.count";
    private static final String PROP_NAME_SPLIT_FACTOR = "default.caching.strategy.splitfactor";
    private Aggregator aggregator = null;
    private PropertySection propertySection = null;
    private CachingStrategyNode nodeDelegate;

    private void reinit() {
        propertySection = null;
        propertySection = getPropertySection();
        if (propertySection != null) {
            List<PropertySet> propertySets = propertySection.getPropertySet();
            for (PropertySet propertySet : propertySets) {
                if (DefaultCachingStrategy.class.getName().equals(propertySet.getId())) {
                    for (Property property : propertySet.getProperties()) {
                        if (property.getValue() != null) {
                            try {
                                if (PROP_NAME_MAX_ELEMENT_COUNT.equals(property.getId())) {
                                    getTileManager().setMaxElementsPerTile(Integer.parseInt(property.getValue()));
                                } else if (PROP_NAME_SPLIT_FACTOR.equals(property.getId())) {
                                    getTileManager().setSplitFactor(Integer.parseInt(property.getValue()));
                                }
                            } catch (Throwable e) {
                                LOG.log(Level.SEVERE, e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public PropertySection getPropertySection() {
        if (propertySection == null) {
            if (getAggregator() != null) {
                OUTERLOOP:
                for (ProcessDescriptor descriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (descriptor != null
                            && AggregationProcess.class.getName().equals(descriptor.getJavaType())) {
                        for (PropertySection section : descriptor.getProperties().getSections()) {
                            if (DefaultCachingStrategy.class.getName().equals(section.getId())) {
                                propertySection = section;
                                break OUTERLOOP;
                            }
                        }
                        break;
                    }
                }
            }
            if (propertySection == null) {
                propertySection = createDefaultDescriptor();
            }

        }
        return propertySection;
    }

    private PropertySection createDefaultDescriptor() {
        PropertySection mainSection = new PropertySection();
        mainSection.setId(DefaultCachingStrategy.class.getName());
        mainSection.setName(Bundle.CLT_DefaultCachingStrategy_Name());
        mainSection.setDescription(Bundle.CLT_DefaultCachingStrategy_Description());

        PropertySet propertySet = new PropertySet();
        propertySet.setId(DefaultCachingStrategy.class.getName());
        propertySet.setName(Bundle.CLT_DefaultCachingStrategy_Name());
        propertySet.setDescription(Bundle.CLT_DefaultCachingStrategy_Description());

        Property property = new Property();
        property.setId(PROP_NAME_MAX_ELEMENT_COUNT);
        property.setJavaType(Integer.class.getName());
        property.setValue("2000");
        property.setName(Bundle.CLT_DefaultCachingStrategy_Property_MaxElementPerTile_Name());
        property.setDescription(Bundle.CLT_DefaultCachingStrategy_Property_MaxElementPerTile_Description());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_SPLIT_FACTOR);
        property.setJavaType(Integer.class.getName());
        property.setValue("3");
        property.setName(Bundle.CLT_DefaultCachingStrategy_Property_SplitFactor_Name());
        property.setDescription(Bundle.CLT_DefaultCachingStrategy_Property_Splitfactor_Description());
        propertySet.getProperties().add(property);

        mainSection.getPropertySet().add(propertySet);
        return mainSection;
    }

    @Override
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        reinit();
    }

    @Override
    public Aggregator getAggregator() {
        return this.aggregator;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new CachingStrategyNode(DefaultCachingStrategy.this);
        }
        return nodeDelegate;
    }

    private static class CachingStrategyNode extends AbstractNode {

        private final DefaultCachingStrategy strategy;

        public CachingStrategyNode(DefaultCachingStrategy strategy) {
            super(Children.LEAF);
            this.strategy = strategy;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            PropertySet[] propertySets = sheet.toArray();

            for (PropertySet propertySet : propertySets) {
                sheet.remove(propertySet.getName());
            }

            if (this.strategy != null && this.strategy.getPropertySection() != null) {
                PropertySection propertySection = this.strategy.getPropertySection();
                if (propertySection != null) {
                    List<de.fub.mapsforge.project.aggregator.xml.PropertySet> propertySet = propertySection.getPropertySet();

                    for (de.fub.mapsforge.project.aggregator.xml.PropertySet propSet : propertySet) {
                        Sheet.Set set = Sheet.createPropertiesSet();
                        set.setName(propSet.getId());
                        set.setDisplayName(propSet.getName());
                        set.setShortDescription(propSet.getDescription());
                        sheet.put(set);

                        for (de.fub.mapsforge.project.aggregator.xml.Property property : propSet.getProperties()) {
                            set.put(new NodeProperty(property));
                        }
                    }

                }

            }

            return sheet;
        }
    }
}
