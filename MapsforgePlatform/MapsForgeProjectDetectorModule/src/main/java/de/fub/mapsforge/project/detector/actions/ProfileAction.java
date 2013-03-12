/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.Profile;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
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
        id = "de.fub.mapsforge.project.detector.actions.ProfileAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_ProfileAction")
@ActionReference(
        path = "Loaders/text/detector+xml/Actions",
        position = 1250)
@Messages("CTL_ProfileAction=Profiles")
public final class ProfileAction extends AbstractAction implements Presenter.Popup {

    private static final long serialVersionUID = 1L;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JMenu menutItem = null;

    public ProfileAction() {
        super(Bundle.CTL_ProfileAction());
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }

    @Override
    public JMenuItem getPopupPresenter() {
        menutItem = new JMenu(Bundle.CTL_ProfileAction());
        buttonGroup = new ButtonGroup();
        Detector detector = Utilities.actionsGlobalContext().lookup(Detector.class);
        if (detector != null) {
            JCheckBoxMenuItem checkBoxMenuItem = null;
            for (Profile profile : detector.getDetectorDescriptor().getProfiles().getProfileList()) {
                checkBoxMenuItem = new JCheckBoxMenuItem(new CheckBoxAction(detector, profile));
                checkBoxMenuItem.setSelected(profile.equals(detector.getCurrentActiveProfile()));
                checkBoxMenuItem.setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
                buttonGroup.add(checkBoxMenuItem);
                menutItem.add(checkBoxMenuItem);
            }
        }
        return menutItem;
    }

    private static class CheckBoxAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final Profile profile;
        private final Detector detector;

        public CheckBoxAction(Detector detector, Profile profile) {
            super(profile.getName());
            this.profile = profile;
            this.detector = detector;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            detector.setCurrentActiveProfile(profile);
            detector.notifyModified();
        }
    }
}
