/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.snapshot.api;

import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Serdar
 */
@ActionID(
        category = "SnapShot",
        id = "de.fub.maps.snapshot.api.SnapShotExporterDelegateAction")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_SnapShotExporterAction")
@ActionReference(path = "Toolbars/SnapShot", position = 6)
@NbBundle.Messages("CTL_SnapShotExporterAction=Export")
public final class SnapShotExporterDelegateAction extends AbstractAction implements Presenter.Toolbar, LookupListener {

    @StaticResource
    private static final String EXPORT_ICON_PATH = "de/fub/maps/project/snapshot/exportIcon.png";
    private static final long serialVersionUID = 1L;
    private ComponentSnapShotExporter defaultExporter = null;
    private HashMap<JCheckBoxMenuItem, ComponentSnapShotExporter> exporterMap = new HashMap<JCheckBoxMenuItem, ComponentSnapShotExporter>();
    private JButton button;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private Lookup.Result<Component> componentListener;

    public SnapShotExporterDelegateAction() {
        super();
        componentListener = Utilities.actionsGlobalContext().lookupResult(Component.class);
        componentListener.addLookupListener(SnapShotExporterDelegateAction.this);
        resultChanged(new LookupEvent(componentListener));
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
                try {
                    ComponentSnapShotExporter instance = clazz.newInstance();
                    if (instance != null) {
                        exporterSet.add(instance);
                    }
                } catch (InstantiationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
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


            button = DropDownButtonFactory.createDropDownButton(getIcon(), jPopupMenu);
            button.addActionListener(SnapShotExporterDelegateAction.this);
        }
        return button;
    }

    private Icon getIcon() {
        Image image = IconRegister.findRegisteredIcon("exportIcon.png");
        return image == null
                ? ImageUtilities.loadImageIcon(EXPORT_ICON_PATH, false)
                : new ImageIcon(image);
    }

    @Override
    public Component getToolbarPresenter() {
        return getPresenter();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (button != null) {
            boolean active = !componentListener.allInstances().isEmpty();
            button.setEnabled(active);
        }
    }
}
