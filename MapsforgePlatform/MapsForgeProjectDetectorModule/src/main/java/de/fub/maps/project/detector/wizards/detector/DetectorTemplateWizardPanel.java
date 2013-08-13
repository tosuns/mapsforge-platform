/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.wizards.detector;

import de.fub.maps.project.detector.model.Detector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class DetectorTemplateWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private DetectorTemplateVisualPanel component;
    private final ChangeSupport cs = new ChangeSupport(this);
    public static final String PROP_NAME_TEMPLATE_INSTANCE = "DetectorTemplateWizardPanel.template.instance";

    @Override
    public DetectorTemplateVisualPanel getComponent() {
        if (component == null) {
            component = new DetectorTemplateVisualPanel();
            component.getExplorerManager().addPropertyChangeListener(WeakListeners.propertyChange(DetectorTemplateWizardPanel.this, component.getExplorerManager()));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return getComponent().getExplorerManager().getSelectedNodes().length > 0;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        Node[] selectedNodes = getComponent().getExplorerManager().getSelectedNodes();
        if (selectedNodes.length == 1) {
            Detector template = selectedNodes[0].getLookup().lookup(Detector.class);
            wiz.putProperty(PROP_NAME_TEMPLATE_INSTANCE, template);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            cs.fireChange();
        }
    }
}
