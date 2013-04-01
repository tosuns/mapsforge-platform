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
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
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
@NbBundle.Messages({"CLT_Progress_name=Pipeline Process", "# {0} - current phase", "CLT_Progress_Phase=Phase {0}"})
public class AggregatorNode extends DataNode implements PropertyChangeListener, ChangeListener {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/aggregationBuilderIcon.png";
    protected static final String TAB_NAME = "tabName";
    private HashMap<String, Sheet.Set> setMap = new HashMap<String, Sheet.Set>();
    private Sheet sheet;
    private final Aggregator aggregator;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public AggregatorNode(Aggregator aggregator) {
        super(aggregator.getDataObject(), Children.create(new AggregatorSubFolderFactory(aggregator), true), new ProxyLookup(Lookups.fixed(aggregator), aggregator.getDataObject().getLookup()));
        this.aggregator = aggregator;
        this.aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregatorNode.this, aggregator));
        sheet = Sheet.createDefault();
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
    protected Sheet createSheet() {
        if (aggregator != null) {
            final AggregatorDescriptor descriptor = aggregator.getAggregatorDescriptor();

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
        set.setValue(TAB_NAME, MessageFormat.format("{0}", tabName, id));

        for (de.fub.mapsforge.project.aggregator.xml.Property property : propertySet.getProperties()) {
            Property<?> prop = new ProcessProperty(modelSynchronizerClient, property) {
                @Override
                public boolean canRead() {
                    return super.canRead(); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean canWrite() {
                    return aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING;
                }
            };
            set.put(prop);
        }
        return set;
    }

    private Sheet.Set createPropertySection(PropertySection section) {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName(section.getName());
        set.setDisplayName(section.getName());

        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
            for (de.fub.mapsforge.project.aggregator.xml.Property property : propertySet.getProperties()) {
                ProcessProperty processProperty = new ProcessProperty(modelSynchronizerClient, property) {
                    @Override
                    public boolean canRead() {
                        return super.canRead();
                    }

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
        "CLT_Property_Description_Description=Description test of the aggregator",
        "CLT_Property_Aggregator_Strategy_Class_Name=Aggregation Strategy",
        "CLT_Property_Aggregator_Strategy_Class_Description=The aggregation strategy that wil be used during aggregation.",
        "CLT_Property_TileCache_Strategy_Class_Name=Tilecache Strategy",
        "CLT_Property_TileCache_Strategy_Class_Description=The TileCache strategy which will be used during the aggregation."
    })
    private Sheet.Set createProperties(final AggregatorDescriptor descriptor) {
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
            public boolean canRead() {
                return super.canRead();
            }

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
            public boolean canRead() {
                return super.canRead();
            }

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

        property = new ClassProperty(
                "aggStrategy",
                Bundle.CLT_Property_Aggregator_Strategy_Class_Name(),
                Bundle.CLT_Property_Aggregator_Strategy_Class_Description(),
                IAggregationStrategy.class) {
            private ClassWrapper wrapper = aggregator.getAggregatorDescriptor().getAggregationStrategy() != null
                    ? new ClassWrapper(aggregator.getAggregatorDescriptor().getAggregationStrategy()) : null;

            @Override
            public boolean canRead() {
                return super.canRead();
            }

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
        };
        set.put(property);

        property = new ClassProperty(
                "cacheStrategy",
                Bundle.CLT_Property_TileCache_Strategy_Class_Name(),
                Bundle.CLT_Property_TileCache_Strategy_Class_Description(),
                ICachingStrategy.class) {
            private ClassWrapper wrapper = aggregator.getAggregatorDescriptor().getTileCachingStrategy() != null
                    ? new ClassWrapper(aggregator.getAggregatorDescriptor().getTileCachingStrategy()) : null;

            @Override
            public boolean canRead() {
                return super.canRead();
            }

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
                        throw new IllegalArgumentException("null is not a valid argument!");
                    }
                    wrapper = val;
                    aggregator.getAggregatorDescriptor().setTileCachingStrategy(val.getQualifiedName());

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
}
