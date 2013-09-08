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
package de.fub.maps.project.detector.model.inference.actions;

import de.fub.maps.project.api.process.ProcessState;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.inference.InferenceMode;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Serdar
 */
@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.model.inference.actions.ToolbarDetectorStartAction")
@ActionRegistration(
        lazy = true,
        displayName = "#CLT_ToolbarDetectorStartAction_Name")
@ActionReferences({
    @ActionReference(path = "Projects/org-maps-project/Detector/Toolbar/Start/Actions", position = 2000, separatorAfter = 2100)
})
@NbBundle.Messages({
    "CLT_ToolbarDetectorStartAction_Name=Start"
})
public class ToolbarDetectorStartAction extends AbstractAction implements Presenter.Toolbar {

    private static final long serialVersionUID = 1L;
    private final Detector detector;
    private DetectorListener dl;

    public ToolbarDetectorStartAction(Detector context) {
        super();
        this.detector = context;
        setEnabled(this.detector != null);
        Image icon = IconRegister.findRegisteredIcon("toolbarProcessRunIcon.png");
        if (icon != null) {
            putValue(Action.SMALL_ICON, new ImageIcon(icon));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                detector.getInferenceModel().setInferenceMode(InferenceMode.ALL_MODE);
                detector.start();
            }
        });
    }

    @Override
    public Component getToolbarPresenter() {
        final JButton processStartButton;
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-maps-project/Detector/Toolbar/Start/Popup/Actions");
        JPopupMenu popupMenu = new JPopupMenu();

        for (Action action : actionsForPath) {
            JMenuItem menuItem = new JMenuItem(action);
            popupMenu.add(menuItem);
        }

        processStartButton = DropDownButtonFactory.createDropDownButton((Icon) getValue(Action.SMALL_ICON), popupMenu);
        processStartButton.setAction(ToolbarDetectorStartAction.this);
        if (detector != null) {
            dl = new DetectorListener(processStartButton);
            detector.addPropertyChangeListener(WeakListeners.propertyChange(dl, detector));
        }
        return processStartButton;
    }

    private class DetectorListener implements PropertyChangeListener {

        private final JButton processStartButton;

        public DetectorListener(JButton processStartButton) {
            this.processStartButton = processStartButton;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            processStartButton.setEnabled(detector != null && detector.getDetectorState() != ProcessState.RUNNING);
        }
    }
}
