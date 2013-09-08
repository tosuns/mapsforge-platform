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
package de.fub.maps.project.datasource.spi.actions;

import de.fub.maps.project.datasource.ui.MapViewElement;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Serdar
 */
public class SplitpanelAction extends AbstractAction {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/datasource/ui/splitPanelIcon.png";
    private static final long serialVersionUID = 1L;
    private final MapViewElement mapViewElement;

    public SplitpanelAction(MapViewElement mapViewElement) {
        this.mapViewElement = mapViewElement;
        putValue(Action.SMALL_ICON, ImageUtilities.loadImage(ICON_PATH));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (mapViewElement != null) {
            mapViewElement.setSplitPanelVisible(!mapViewElement.isSplitpanelVisible());
        }
    }
}
