/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.actions;

import de.fub.maps.project.api.process.ProcessState;
import de.fub.maps.project.detector.model.Detector;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.actions.DetectorStartAction")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_DetecotStartAction")
@ActionReference(path = "Loaders/text/detector+xml/Actions",
        position = 250,
        separatorAfter = 275)
@Messages("CTL_DetecotStartAction=Start")
public final class DetectorStartAction extends AbstractAction implements Presenter.Popup {

    private static final long serialVersionUID = 1L;
    private DetectorListener dl;

    public DetectorStartAction() {
        super(Bundle.CTL_DetecotStartAction());
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // do nothing
    }

    @Override
    public JMenuItem getPopupPresenter() {
        final JMenu menu = new JMenu(DetectorStartAction.this);
        final Detector detector = Utilities.actionsGlobalContext().lookup(Detector.class);
        if (detector != null) {

            List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-maps-project/Detector/Start/Actions");
            JMenuItem item = null;
            for (Action action : actionsForPath) {
                item = new JMenuItem(action);
                item.setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
                menu.add(item);
            }
            dl = new DetectorListener(menu, detector);
            menu.setEnabled(detector.getInferenceModel() != null);
            detector.addPropertyChangeListener(WeakListeners.propertyChange(dl, detector));
        } else {
            menu.setEnabled(false);
        }
        return menu;
    }

    private static class DetectorListener implements PropertyChangeListener {

        private final JMenu menu;
        private final Detector detector;

        public DetectorListener(JMenu menu, Detector detector) {
            this.menu = menu;
            this.detector = detector;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            menu.setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
        }
    }
}
