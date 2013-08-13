/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.actions.wizards.aggregator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class AggregatorWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AggregatorVisualPanel2 component;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    private WizardDescriptor wiz;

    @Override
    public AggregatorVisualPanel2 getComponent() {
        if (component == null) {
            component = new AggregatorVisualPanel2();
            component.getExplorerManager().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    changeSupport.fireChange();
                }
            });
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
        return getComponent().getExplorerManager().getSelectedNodes().length == 1;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        Object property = this.wiz.getProperty(AggregatorWizardAction.PROP_NAME_TEMPLATE);
        if (property instanceof DataObject) {
            try {
                getComponent().getExplorerManager().setSelectedNodes(new Node[]{new AggregatorVisualPanel2.FileObjectWrapperNode((DataObject) property)});
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        Node[] selectedNodes = getComponent().getExplorerManager().getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node node = selectedNodes[0];
            DataObject dataObject = node.getLookup().lookup(DataObject.class);
            if (dataObject != null) {
                wiz.putProperty(AggregatorWizardAction.PROP_NAME_TEMPLATE, dataObject);
            }
        }
    }
}
