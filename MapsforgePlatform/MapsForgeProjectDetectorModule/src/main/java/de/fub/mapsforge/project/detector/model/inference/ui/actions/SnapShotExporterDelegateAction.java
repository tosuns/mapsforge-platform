/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui.actions;

import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.mapsforge.snapshot.api.ComponentSnapShotExporter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author Serdar
 */
public class SnapShotExporterDelegateAction extends AbstractAction {

    @StaticResource
    private static final String EXPORT_ICON_PATH = "de/fub/mapsforge/project/detector/model/inference/ui/exportIcon.png";
    private static final long serialVersionUID = 1L;
    private ComponentSnapShotExporter defaultExporter = null;
    private HashMap<JCheckBoxMenuItem, ComponentSnapShotExporter> exporterMap = new HashMap<JCheckBoxMenuItem, ComponentSnapShotExporter>();
    private JButton button;
    private ButtonGroup buttonGroup = new ButtonGroup();

    public SnapShotExporterDelegateAction() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem) {
            ComponentSnapShotExporter exporter = exporterMap.get((JCheckBoxMenuItem) e.getSource());
            if (exporter != null) {
                defaultExporter = exporter;
            }
        } else {
            if (defaultExporter != null) {
                Collection<? extends Component> allInstances = Utilities.actionsGlobalContext().lookupResult(Component.class).allInstances();
                if (!allInstances.isEmpty()) {
                    for (Component component : allInstances) {
                        defaultExporter.export(component);
                    }
                }
            }
        }
    }

    public JButton getPresenter() {
        if (button == null) {
            JPopupMenu jPopupMenu = new JPopupMenu();
            SortedSet<ComponentSnapShotExporter> exporterSet = new TreeSet<ComponentSnapShotExporter>();

            Set<Class<? extends ComponentSnapShotExporter>> allClasses = Lookup.getDefault().lookupResult(ComponentSnapShotExporter.class).allClasses();

            for (Class<? extends ComponentSnapShotExporter> clazz : allClasses) {
                ComponentSnapShotExporter instance = DetectorUtils.createInstance(clazz, clazz.getName());
                if (instance != null) {
                    exporterSet.add(instance);
                }
            }

            boolean defaultSelected = false;

            for (ComponentSnapShotExporter exporter : exporterSet) {
                JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(exporter.getName(), new ImageIcon(exporter.getIconImage()));
                buttonGroup.add(checkBoxMenuItem);
                checkBoxMenuItem.setToolTipText(exporter.getShortDescription());
                checkBoxMenuItem.addActionListener(SnapShotExporterDelegateAction.this);
                if (!defaultSelected) {
                    defaultSelected = true;
                    defaultExporter = exporter;
                    checkBoxMenuItem.doClick();
                }
                exporterMap.put(checkBoxMenuItem, exporter);
                jPopupMenu.add(checkBoxMenuItem);
            }

            button = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon(EXPORT_ICON_PATH, false), jPopupMenu);
            button.addActionListener(SnapShotExporterDelegateAction.this);
        }
        return button;
    }
}
