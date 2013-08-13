/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.wizard;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class MapsWizardPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {

    private WizardDescriptor wizardDescriptor;
    private MapsPanelVisual component;
    private transient final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0

    public MapsWizardPanel() {
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new MapsPanelVisual(this);
            component.setName(NbBundle.getMessage(MapsWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("de.fub.mapforgeproject.MapsForgeWizardPanel");
    }

    @Override
    public boolean isValid() {
        if (getComponent() instanceof MapsPanelVisual) {
            return ((MapsPanelVisual) getComponent()).valid(wizardDescriptor);
        }
        return false;
    }

    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    protected final void fireChangeEvent() {
        Set<ChangeListener> ls;
        synchronized (listeners) {
            ls = new HashSet<ChangeListener>(listeners);
        }
        ChangeEvent ev = new ChangeEvent(this);
        for (ChangeListener l : ls) {
            l.stateChanged(ev);
        }
    }

    @Override
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        if (getComponent() instanceof MapsPanelVisual) {
            ((MapsPanelVisual) getComponent()).read(wizardDescriptor);
        }
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        if (getComponent() instanceof MapsPanelVisual) {
            ((MapsPanelVisual) getComponent()).store(d);
        }
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
}
