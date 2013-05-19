/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.mapforgeproject.api.process.ProcessNode;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.utilsmodule.node.property.NodeProperty;
import java.awt.Image;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class AggregationProcessNode extends ProcessNode implements ChangeListener {

    protected static final String TAB_NAME = "tabName";
    @StaticResource
    private static final String PROCESS_ICON_NORMAL = "de/fub/mapsforge/project/aggregator/processIconNormal.png";
    @StaticResource
    private static final String PROCESS_ICON_RUN = "de/fub/mapsforge/project/aggregator/processIconRun.png";
    @StaticResource
    private static final String PROCESS_ICON_ERROR = "de/fub/mapsforge/project/aggregator/processIconError.png";
    private final AbstractAggregationProcess<?, ?> abstractProcess;

    public AggregationProcessNode(AbstractAggregationProcess<?, ?> process) {
        super(process);
        this.abstractProcess = process;
        process.addPropertyChangeListener(WeakListeners.propertyChange(AggregationProcessNode.this, process));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        PropertySet[] propertySets = sheet.toArray();

        for (PropertySet set : propertySets) {
            sheet.remove(set.getName());
        }

        if (abstractProcess != null) {
            ProcessDescriptor descriptor = abstractProcess.getProcessDescriptor();

            if (descriptor != null) {
                List<PropertySection> sections = descriptor.getProperties().getSections();

                if (sections != null) {
                    NodeProperty processProperty = null;
                    for (PropertySection section : sections) {

                        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                            Sheet.Set set = Sheet.createPropertiesSet();
                            sheet.put(set);
                            set.setName(propertySet.getId());
                            set.setDisplayName(propertySet.getName());
                            set.setShortDescription(propertySet.getDescription());
                            for (de.fub.mapsforge.project.aggregator.xml.Property property : propertySet.getProperties()) {
                                processProperty = new NodeProperty(property);
                                set.put(processProperty);
                            }
                        }
                    }
                }
            }
        }
        return sheet;
    }

    @Override
    public Image getIcon(int type) {
        AbstractAggregationProcess process = getLookup().lookup(AbstractAggregationProcess.class);
        if (process != null) {
            switch (process.getProcessState()) {
                case INACTIVE:
                    return ImageUtilities.loadImage(PROCESS_ICON_NORMAL);
                case RUNNING:
                    return ImageUtilities.loadImage(PROCESS_ICON_RUN);
                case ERROR:
                    return ImageUtilities.loadImage(PROCESS_ICON_ERROR);
                default:
                    break;
            }
        }
        return super.getIcon(type); //fall back
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // nothing
    }
}
