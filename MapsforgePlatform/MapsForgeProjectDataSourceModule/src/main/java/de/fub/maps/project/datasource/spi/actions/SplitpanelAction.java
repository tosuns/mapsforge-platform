/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
