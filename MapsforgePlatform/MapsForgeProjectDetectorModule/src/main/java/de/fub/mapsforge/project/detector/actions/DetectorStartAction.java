/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.model.Detector;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.DetectorStartAction")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_DetecotStartAction")
@ActionReference(path = "Loaders/text/detector+xml/Actions",
        position = 250,
        separatorAfter = 275)
@Messages("CTL_DetecotStartAction=Start")
public final class DetectorStartAction extends AbstractAction implements Presenter.Popup {

    private static final long serialVersionUID = 1L;

    public DetectorStartAction() {
        super(Bundle.CTL_DetecotStartAction());
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu menu = new JMenu(DetectorStartAction.this);
        Detector detector = Utilities.actionsGlobalContext().lookup(Detector.class);
        if (detector != null) {

            List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-mapsforge-project/Detector/Start/Actions");
            JMenuItem item = null;
            for (Action action : actionsForPath) {
                item = new JMenuItem(action);
                item.setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
                menu.add(item);
            }
            menu.setEnabled(detector.getInferenceModel() != null);
        } else {
            menu.setEnabled(false);
        }
        return menu;
    }
}
