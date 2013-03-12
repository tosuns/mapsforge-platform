/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages("CLT_SaveAsHtmlAction_Name=Save as html 5")
public class SaveAsHtmlAction extends AbstractAction {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/detector/model/inference/ui/actions/html5Icon.png";
    private static final long serialVersionUID = 1L;
    private JComponent component;

    public SaveAsHtmlAction() {
        super(null, ImageUtilities.loadImageIcon(ICON_PATH, false));
        putValue(Action.SHORT_DESCRIPTION, Bundle.CLT_SaveAsHtmlAction_Name());
    }

    public SaveAsHtmlAction(JComponent component) {
        this();
        this.component = component;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (component != null) {
            // TODO something
        }
    }
}
