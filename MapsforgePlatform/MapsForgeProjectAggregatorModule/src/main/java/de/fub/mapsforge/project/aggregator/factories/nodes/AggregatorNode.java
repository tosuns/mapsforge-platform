/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories.nodes;

import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.mapsforge.project.aggregator.factories.AggregatorSubFolderFactory;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassProperty;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassWrapper;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.AggregatorDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptorList;
import de.fub.mapsforge.project.aggregator.xml.Properties;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.ReadOnly;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({"CLT_Progress_name=Pipeline Process", "# {0} - current phase", "CLT_Progress_Phase=Phase {0}"})
public class AggregatorNode extends DataNode implements PropertyChangeListener, ChangeListener {

    protected static final String TAB_NAME = "tabName";
    private HashMap<String, Sheet.Set> setMap = new HashMap<String, Sheet.Set>();
    private Sheet sheet;
    private final Aggregator aggregator;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public AggregatorNode(Aggregator aggregator) {
        super(aggregator.getDataObject(), Children.create(new AggregatorSubFolderFactory(aggregator), true), new ProxyLookup(Lookups.fixed(aggregator), aggregator.getDataObject().getLookup()));
        aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregatorNode.this, aggregator));
        sheet = Sheet.createDefault();
        this.aggregator = aggregator;
        modelSynchronizerClient = aggregator.create(AggregatorNode.this);
    }

    @Override
    public String getDisplayName() {
        if (aggregator != null) {
            return aggregator.getDescriptor().getName();
        }
        return super.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        if (aggregator != null) {
            return aggregator.getDescriptor().getDescription();
        }
        return super.getShortDescription();
    }

    @Override
    protected Sheet createSheet() {
        if (aggregator != null) {
            final AggregatorDescriptor descriptor = aggregator.getDescriptor();

            if (descriptor != null) {
                Sheet.Set createProperties = createProperties(descriptor);
                setMap.put(createProperties.getName(), createProperties);
                sheet.put(createProperties);

                Properties properties = descriptor.getProperties();
                if (properties != null) {

                    for (PropertySection section : properties.getSections()) {
                        Sheet.Set createPropertySection = createPropertySection(section);
                        setMap.put(createPropertySection.getName(), createProperties);
                        sheet.put(createPropertySection);
                    }
                }
                ProcessDescriptorList pipeline = descriptor.getPipeline();
                if (pipeline != null) {
                    List<ProcessDescriptor> processDescriptors = Collections.unmodifiableList(pipeline.getList());
                    if (processDescriptors != null) {
                        for (int index = 0; index < processDescriptors.size(); index++) {
                            ProcessDescriptor processDescriptor = processDescriptors.get(index);
                            if (processDescriptor != null) {
                                properties = processDescriptor.getProperties();
                                if (properties != null) {
                                    for (PropertySection section : properties.getSections()) {
                                        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                                            Sheet.Set createProcessProperty = createProcessProperty(index, propertySet, processDescriptor.getDisplayName());
                                            setMap.put(createProcessProperty.getName(), createProcessProperty);
                                            sheet.put(createProcessProperty);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            sheet.put(createPipelineProperty(aggregator));
        }
        return sheet;
    }

    private Sheet.Set createProcessProperty(int id, de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet, String tabName) {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName(propertySet.getName() + id);
        set.setDisplayName(propertySet.getName());
        set.setShortDescription(propertySet.getDescription());
        set.setValue(TAB_NAME, MessageFormat.format("{0} ({1})", tabName, id));

        for (de.fub.mapsforge.project.aggregator.xml.Property property : propertySet.getProperties()) {
            Property<?> prop = new ProcessProperty(modelSynchronizerClient, property);
            set.put(prop);
        }
        return set;
    }

    private Property<?> createProperty(final de.fub.mapsforge.project.aggregator.xml.Property propertyItem) {
        Property<?> property = null;

        if (Boolean.class.getName().equals(propertyItem.getJavaType())) {
            property = new PropertySupport.ReadWrite<Boolean>(propertyItem.getName(), Boolean.class, propertyItem.getName(), "") {
                private Boolean value = Boolean.parseBoolean(propertyItem.getValue());

                @Override
                public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                    return value;
                }

                @Override
                public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (!value.equals(val) && aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                        value = val;
                        propertyItem.setValue(String.valueOf(val));
                        update();
                    }
                }
            };
            property.setName(propertyItem.getName());
        } else if (Double.class.getName().equals(propertyItem.getJavaType())) {
            property = new PropertySupport.ReadWrite<Double>(propertyItem.getName(), Double.class, propertyItem.getName(), "") {
                private Double value = Double.parseDouble(propertyItem.getValue());

                @Override
                public Double getValue() throws IllegalAccessException, InvocationTargetException {
                    return value;
                }

                @Override
                public void setValue(Double val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (!value.equals(val) && aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                        value = val;
                        propertyItem.setValue(String.valueOf(val));
                        update();
                    }
                }
            };
            property.setName(propertyItem.getName());
        } else if (Integer.class.getName().equals(propertyItem.getJavaType())) {
            property = new PropertySupport.ReadWrite<Integer>(propertyItem.getName(), Integer.class, propertyItem.getName(), "") {
                private Integer value = Integer.parseInt(propertyItem.getValue());

                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return value;
                }

                @Override
                public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (!value.equals(val)) {
                        value = val;
                        propertyItem.setValue(String.valueOf(val));
                        update();
                    }
                }
            };
            property.setName(propertyItem.getName());
        } else if (Long.class.getName().equals(propertyItem.getJavaType())) {
            property = new PropertySupport.ReadWrite<Long>(propertyItem.getName(), Long.class, propertyItem.getName(), "") {
                private Long value = Long.valueOf(propertyItem.getValue());

                @Override
                public Long getValue() throws IllegalAccessException, InvocationTargetException {
                    return value;
                }

                @Override
                public void setValue(Long val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (!value.equals(val) && aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                        value = val;
                        propertyItem.setValue(String.valueOf(val));
                        update();
                    }
                }
            };
            property.setName(propertyItem.getName());
        } else if (Color.class.getName().equals(propertyItem.getJavaType())) {
            property = new PropertySupport.ReadWrite<Color>(propertyItem.getName(), Color.class, propertyItem.getName(), "") {
                private Color color = new Color(Integer.parseInt(propertyItem.getValue()));

                @Override
                public Color getValue() throws IllegalAccessException, InvocationTargetException {
                    return color;
                }

                @Override
                public void setValue(Color val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (val != null && color.getRGB() != val.getRGB() && aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                        color = val;
                        propertyItem.setValue(String.valueOf(val.getRGB()));
                        update();
                    }
                }
            };
            property.setName(propertyItem.getName());
        } else if (String.class.getName().equals(propertyItem.getJavaType())) {
            property = new PropertySupport.ReadWrite<String>(propertyItem.getName(), String.class, propertyItem.getName(), "") {
                private String value = propertyItem.getValue();

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return value;
                }

                @Override
                public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (!value.equals(val) && aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                        value = val;
                        propertyItem.setValue(val);
                        update();
                    }
                }
            };
            property.setName(propertyItem.getName());
        } else { // TODO subclass of enums
        }


        return property;
    }

    private Sheet.Set createPropertySection(PropertySection section) {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName(section.getName());
        set.setDisplayName(section.getName());

        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
            for (de.fub.mapsforge.project.aggregator.xml.Property property : propertySet.getProperties()) {
                set.put(new Prop(property));
            }
        }

        return set;
    }

    @NbBundle.Messages("CLT_Common_info=General Information")
    private Sheet.Set createProperties(final AggregatorDescriptor descriptor) {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("general information");
        set.setDisplayName(Bundle.CLT_Common_info());
        Property<?> property = null;

        property = new PropertySupport.ReadWrite<String>("name", String.class, "Name", "Name of the aggregator.") {
            private String value = aggregator.getDescriptor().getName();

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return value;
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    value = val;
                    aggregator.getDescriptor().setName(val);
                    update();
                }
            }
        };
        set.put(property);

        property = new PropertySupport.ReadWrite<String>("description", String.class, "Description", "Description text for the aggregator") {
            private String description = aggregator.getDescriptor().getDescription();

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return description;
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    description = val;
                    aggregator.getDescriptor().setDescription(val);
                    update();
                }
            }
        };
        set.put(property);

        property = new ClassProperty("aggStrategy", "Aggregator Strategy", "something", IAggregationStrategy.class) {
            private ClassWrapper wrapper = aggregator.getDescriptor().getAggregationStrategy() != null
                    ? new ClassWrapper(aggregator.getDescriptor().getAggregationStrategy()) : null;

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    if (val == null) {
                        throw new IllegalArgumentException(" null is not a valid argument!");
                    }
                    wrapper = val;
                    aggregator.getDescriptor().setAggregationStrategy(val.getQualifiedName());
                    update();
                }
            }
        };
        set.put(property);


        property = new ClassProperty("cacheStrategy", "TileCache Strategy", "something", ICachingStrategy.class) {
            private ClassWrapper wrapper = aggregator.getDescriptor().getTileCachingStrategy() != null
                    ? new ClassWrapper(aggregator.getDescriptor().getTileCachingStrategy()) : null;

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    if (val == null) {
                        throw new IllegalArgumentException("null is not a valid argument!");
                    }
                    wrapper = val;
                    aggregator.getDescriptor().setTileCachingStrategy(val.getQualifiedName());

                    update();
                }
            }
        };
        set.put(property);


        return set;
    }

    @NbBundle.Messages("CLT_Pipeline_Content=Pipeline Content")
    private Sheet.Set createPipelineProperty(Aggregator aggregator) {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("Pipline Content");
        set.setDisplayName(Bundle.CLT_Progress_name());

        Property<?> property = null;
        Iterator<AbstractAggregationProcess<?, ?>> it = aggregator.getPipeline().iterator();
        int i = 1;
        while (it.hasNext()) {
            final AbstractAggregationProcess<?, ?> process = it.next();
            if (process.getDescriptor() != null) {
                String name = Bundle.CLT_Progress_Phase(i);
                property = new ReadOnly<String>(name, String.class, name, process.getDescription()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return process.getName();
                    }
                };
                set.put(property);
                i++;
            }
        }
        return set;
    }

    @Override
    public Action getPreferredAction() {
        return getActions(true)[0];
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actionsForPath = new ArrayList<Action>(Utilities.actionsForPath("Loaders/text/aggregationbuilder+xml/Actions"));
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        if (aggregator != null) {
            image = aggregator.getAggregatorState().getImage();
        }
        return image;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Aggregator.PROP_NAME_AGGREGATOR_STATE.equals(evt.getPropertyName())) {
            fireIconChange();
        }
    }

    private void update() {
        aggregator.notifyModified();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        for (PropertySet set : sheet.toArray()) {
            sheet.remove(set.getName());
        }
        setMap.clear();
        createSheet();
        for (Sheet.Set set : setMap.values()) {
            sheet.put(set);
        }
        fireIconChange();
    }

    private static class Prop extends PropertySupport.ReadWrite<String> {

        private final de.fub.mapsforge.project.aggregator.xml.Property property;

        public Prop(de.fub.mapsforge.project.aggregator.xml.Property property) {
            super(property.getName(), String.class, property.getName(), "");
            this.property = property;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return property.getValue();
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            property.setValue(val);
        }
    }
}
