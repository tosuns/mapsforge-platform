/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.actions;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
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
import org.openide.util.actions.Presenter;

/**
 *
 * @author Serdar
 */
@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.model.inference.actions.ToolbarDetectorStartAction")
@ActionRegistration(
        lazy = true,
        displayName = "#CLT_ToolbarDetectorStartAction_Name")
@ActionReferences({
    @ActionReference(path = "Projects/org-mapsforge-project/Detector/Toolbar/Start/Actions", position = 2000, separatorAfter = 2100)
})
@NbBundle.Messages({
    "CLT_ToolbarDetectorStartAction_Name=Start"
})
public class ToolbarDetectorStartAction extends AbstractAction implements Presenter.Toolbar {

    private static final long serialVersionUID = 1L;
    private final Detector detector;

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
        JButton processStartButton = new JButton(this);
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-mapsforge-project/Detector/Toolbar/Start/Popup/Actions");
        JPopupMenu popupMenu = new JPopupMenu();

        for (Action action : actionsForPath) {
            JMenuItem menuItem = new JMenuItem(action);
            popupMenu.add(menuItem);
        }

        processStartButton = DropDownButtonFactory.createDropDownButton((Icon) getValue(Action.SMALL_ICON), popupMenu);
        processStartButton.setAction(this);
        return processStartButton;
    }
}
