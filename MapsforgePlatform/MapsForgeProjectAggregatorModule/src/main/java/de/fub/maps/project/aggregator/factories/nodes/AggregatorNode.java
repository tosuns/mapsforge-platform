/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.factories.nodes;

import de.fub.maps.project.aggregator.factories.AggregatorSubFolderFactory;
import de.fub.maps.project.aggregator.factories.nodes.properties.ClassProperty;
import de.fub.maps.project.aggregator.factories.nodes.properties.ClassWrapper;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import de.fub.maps.project.aggregator.xml.Properties;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.ReadOnly;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Progress_name=Pipeline Process",
    "# {0} - current phase",
    "CLT_Progress_Phase=Phase {0}"})
public class AggregatorNode extends DataNode implements PropertyChangeListener, ChangeListener {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/aggregator/filetype/aggregationBuilderIcon.png";
    private static final Logger LOG = Logger.getLogger(AggregatorNode.class.getName());
    protected static final String TAB_NAME = "tabName";
    private Sheet sheet = null;
    private final Aggregator aggregator;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public AggregatorNode(Aggregator aggregator) {
        super(aggregator.getDataObject(), Children.create(new AggregatorSubFolderFactory(aggregator), true), new ProxyLookup(Lookups.fixed(aggregator), aggregator.getDataObject().getLookup()));
        this.aggregator = aggregator;
        this.aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregatorNode.this, aggregator));

        modelSynchronizerClient = this.aggregator.create(AggregatorNode.this);
    }

    @Override
    public String getDisplayName() {
        if (aggregator != null && aggregator.getAggregatorDescriptor() != null) {
            return aggregator.getAggregatorDescriptor().getName();
        }
        return super.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        if (aggregator != null && aggregator.getAggregatorDescriptor() != null) {
            return aggregator.getAggregatorDescriptor().getDescription();
        }
        return super.getShortDescription();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Sheet createSheet() {
        if (sheet == null) {
            sheet = Sheet.createDefault();
        }

        for (PropertySet ps : sheet.toArray()) {
            sheet.remove(ps.getName());
        }

        if (aggregator != null) {
            final AggregatorDescriptor descriptor = aggregator.getAggregatorDescriptor();

            if (descriptor != null) {
                Sheet.Set createProperties = createProperties();
                sheet.put(createProperties);

                Properties properties = descriptor.getProperties();
                if (properties != null) {

                    for (PropertySection section : properties.getSections()) {
                        Sheet.Set createPropertySection = createPropertySection(section);
                        sheet.put(createPropertySection);
                    }
                }
                Collection<AbstractAggregationProcess<?, ?>> processes = aggregator.getPipeline().getProcesses();
                for (AbstractAggregationProcess<?, ?> process : processes) {
                    PropertySet[] propertySets = new ProcessFilterNode(process.getNodeDelegate()).getPropertySets();

                    for (PropertySet propertySet : propertySets) {
                        Sheet.Set set = Sheet.createPropertiesSet();
                        set.setName(propertySet.getName());
                        set.setDisplayName(propertySet.getDisplayName());
                        set.setShortDescription(propertySet.getShortDescription());
                        set.setValue(TAB_NAME, process.getName());
                        sheet.put(set);

                        for (Property property : propertySet.getProperties()) {
                            if (property != null) {
                                set.put(property);
                            }
                        }
                    }

                }
            }
            Sheet.Set createPipelineProperty = createPipelineProperty(aggregator);
            sheet.put(createPipelineProperty);
        }

        return sheet;
    }

    private Sheet.Set createPropertySection(PropertySection section) {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName(section.getId());
        set.setDisplayName(section.getName());
        set.setShortDescription(section.getDescription());

        for (de.fub.maps.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
            for (de.fub.maps.project.aggregator.xml.Property property : propertySet.getProperties()) {
                ProcessProperty processProperty = new ProcessProperty(modelSynchronizerClient, property) {
                    @Override
                    public boolean canWrite() {
                        return aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING;
                    }
                };
                set.put(processProperty);
            }
        }

        return set;
    }

    @NbBundle.Messages({
        "CLT_Common_info=General Information",
        "CLT_Property_Name_DisplayName=Name",
        "CLT_Property_Name_Description=Name of the aggregator.",
        "CLT_Property_Description_DisplayName=Description",
        "CLT_Property_Description_Description=Description test of the aggregator",})
    private Sheet.Set createProperties() {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("general information");
        set.setDisplayName(Bundle.CLT_Common_info());
        Property<?> property = null;

        property = new PropertySupport.ReadWrite<String>(
                "name",
                String.class,
                Bundle.CLT_Property_Name_DisplayName(),
                Bundle.CLT_Property_Name_Description()) {
            private String value = aggregator.getAggregatorDescriptor().getName();

            @Override
            public boolean canWrite() {
                return aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return value;
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    value = val;
                    aggregator.getAggregatorDescriptor().setName(val);
                    update();
                }
            }
        };
        set.put(property);

        property = new PropertySupport.ReadWrite<String>(
                "description",
                String.class,
                Bundle.CLT_Property_Description_DisplayName(),
                Bundle.CLT_Property_Description_Description()) {
            private String description = aggregator.getAggregatorDescriptor().getDescription();

            @Override
            public boolean canWrite() {
                return aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return description;
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                    description = val;
                    aggregator.getAggregatorDescriptor().setDescription(val);
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
            if (process.getProcessDescriptor() != null) {
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
            image = ImageUtilities.loadImage(ICON_PATH);
            Image hint = null;
            switch (aggregator.getAggregatorState()) {
                case ERROR:
                case ERROR_NOT_EXECUTABLE:
                    hint = IconRegister.findRegisteredIcon("errorHintIcon.png");
                    if (hint != null) {
                        image = ImageUtilities.mergeImages(image, hint, 0, 0);
                    }
                    break;
                case INACTIVE:
                    break;
                case WARNING:
                    break;
                case RUNNING:
                    hint = IconRegister.findRegisteredIcon("playHintIcon.png");
                    if (hint != null) {
                        image = ImageUtilities.mergeImages(image, hint, 0, 0);
                    }
                    break;
                default:
                    throw new AssertionError();
            }
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
        aggregator.updateSource();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        createSheet();
        fireIconChange();
    }

    private static class ProcessPropertyWrapper extends PropertySupport.ReadWrite<Object> {

        private final Property<Object> property;
        private final ModelSynchronizer.ModelSynchronizerClient client;

        public ProcessPropertyWrapper(ModelSynchronizer.ModelSynchronizerClient client, Property<Object> property) {
            super(property.getName(), property.getValueType(), property.getDisplayName(), property.getShortDescription());
            this.property = property;
            this.client = client;
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return property.getValue();
        }

        @Override
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            if (property.getValue() != val) {
                property.setValue(val);
                if (client != null) {
                    client.modelChangedFromGui();
                }
            }
        }

        @Override
        public boolean canRead() {
            return property.canRead();
        }

        @Override
        public boolean canWrite() {
            return property.canWrite();
        }

        @Override
        public boolean supportsDefaultValue() {
            return property.supportsDefaultValue();
        }

        @Override
        public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
            property.restoreDefaultValue();
        }

        @Override
        public boolean isDefaultValue() {
            return property.isDefaultValue();
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return property.getPropertyEditor();
        }

        @Override
        public boolean isExpert() {
            return property.isExpert();
        }

        @Override
        public void setExpert(boolean expert) {
            property.setExpert(expert);
        }

        @Override
        public boolean isHidden() {
            return property.isHidden();
        }

        @Override
        public void setHidden(boolean hidden) {
            property.setHidden(hidden);
        }

        @Override
        public boolean isPreferred() {
            return property.isPreferred();
        }

        @Override
        public void setPreferred(boolean preferred) {
            property.setPreferred(preferred);
        }

        @Override
        public void setValue(String attributeName, Object value) {
            property.setValue(attributeName, value);
        }

        @Override
        public Object getValue(String attributeName) {
            return property.getValue(attributeName);
        }

        @Override
        public Enumeration<String> attributeNames() {
            return property.attributeNames();
        }

        @Override
        public String toString() {
            return property.toString();
        }
    }

    private class AggregationStrategyProperty extends ClassProperty {

        public AggregationStrategyProperty(String name, String displayName, String shortDescription, Class<?> interf) {
            super(name, displayName, shortDescription, interf);
        }
        private ClassWrapper wrapper = aggregator.getAggregatorDescriptor().getAggregationStrategy() != null
                ? new ClassWrapper(aggregator.getAggregatorDescriptor().getAggregationStrategy()) : null;

        @Override
        public boolean canWrite() {
            return aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING;
        }

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
                aggregator.getAggregatorDescriptor().setAggregationStrategy(val.getQualifiedName());
                update();
            }
        }
    }
}
