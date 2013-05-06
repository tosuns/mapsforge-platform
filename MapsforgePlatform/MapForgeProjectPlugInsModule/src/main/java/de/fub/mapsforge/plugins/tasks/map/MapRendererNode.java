/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.map;

import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Properties;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Utilities;

/**
 *
 * @author Serdar
 */
public class MapRendererNode extends MapRenderer.MapRendererProcessNode implements ChangeListener {

    private final MapRenderer mapRendererProcess;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public MapRendererNode(MapRenderer mapRenderer) {
        super(Children.create(new MapRendererChildNodeFactory(mapRenderer), true), mapRenderer);
        this.mapRendererProcess = mapRenderer;
        // instance for the node properties
        modelSynchronizerClient = this.mapRendererProcess.getProcessParentDetector().create(MapRendererNode.this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        ProcessDescriptor processDescriptor = mapRendererProcess.getProcessDescriptor();
        Properties properties = processDescriptor.getProperties();
        Property<?> property = null;
        for (de.fub.mapsforge.project.detector.model.xmls.Property xmlProperty : properties.getPropertyList()) {
            if (MapRenderer.PROP_NAME_AGGREGATOR_FILE_PATH.equals(xmlProperty.getId())) {
                property = new AggregatorDataObjectProperty(modelSynchronizerClient, xmlProperty);
                set.put(property);
            }
        }
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Project/Mapsforge/Plugin/Maprenderer/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // do nothing
    }
}
