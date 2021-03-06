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
package de.fub.maps.project.detector.actions;

import de.fub.maps.project.api.process.ProcessState;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.Profile;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.actions.ProfileAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_ProfileAction")
@ActionReference(
        path = "Loaders/text/detector+xml/Actions",
        position = 1250)
@Messages("CTL_ProfileAction=Profiles")
public final class ProfileAction extends AbstractAction
        implements Presenter.Popup, ContextAwareAction, LookupListener, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JMenu menutItem = null;
    private Lookup.Result<Detector> result;
    private Detector detector;

    public ProfileAction() {
        super(Bundle.CTL_ProfileAction());
    }

    public ProfileAction(Lookup context) {
        this();
        result = context.lookupResult(Detector.class);
        result.addLookupListener(ProfileAction.this);
        resultChanged(new LookupEvent(result));
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }

    @Override
    public JMenuItem getPopupPresenter() {
        menutItem = new JMenu(Bundle.CTL_ProfileAction());
        buttonGroup = new ButtonGroup();
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

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new ProfileAction(actionContext);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends Detector> allInstances = result.allInstances();
        if (allInstances.isEmpty()) {
            setEnabled(false);
        } else {
            Detector next = allInstances.iterator().next();
            if (!next.equals(detector)) {
                detector = next;
                detector.addPropertyChangeListener(ProfileAction.this);
                setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (detector != null) {
            setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
        }
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
