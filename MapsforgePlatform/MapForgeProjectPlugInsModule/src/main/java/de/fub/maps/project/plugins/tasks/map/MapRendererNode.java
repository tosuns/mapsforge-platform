/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.plugins.tasks.map;

import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Properties;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.List;
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
        for (de.fub.maps.project.detector.model.xmls.Property xmlProperty : properties.getPropertyList()) {
            if (MapRenderer.PROP_NAME_AGGREGATOR_FILE_PATH.equals(xmlProperty.getId())) {
                property = new AggregatorDataObjectProperty(modelSynchronizerClient, xmlProperty);
                set.put(property);
            }
        }
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Project/Maps/Plugin/Maprenderer/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // do nothing
    }
}
