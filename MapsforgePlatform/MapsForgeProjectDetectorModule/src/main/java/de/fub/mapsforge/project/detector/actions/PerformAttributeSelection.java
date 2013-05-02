/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.ui.AttributeSelectionComponent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.model.inference.actions.PerformAttributeSelection")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_PerformAttributeSelection")
@ActionReferences(
        @ActionReference(
        id =
        @ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.model.inference.actions.PerformAttributeSelection"),
        path = "Loaders/text/detector+xml/Actions", position = 255))
@Messages({"CTL_PerformAttributeSelection=Perform Attribute Selection",
    "CLT_PerformAttributeSelection_Dialog_Title=Attribute Selection Dialog"
})
public final class PerformAttributeSelection extends AbstractAction implements ContextAwareAction, PropertyChangeListener, LookupListener {

    private static final long serialVersionUID = 1L;
    private Detector detector;
    private Lookup.Result<Detector> result;

    public PerformAttributeSelection() {
        super(Bundle.CLT_PerformAttributeSelection_Dialog_Title());
    }

    public PerformAttributeSelection(Lookup context) {
        this();
        result = context.lookupResult(Detector.class);
        result.addLookupListener(PerformAttributeSelection.this);
        resultChanged(new LookupEvent(result));
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        AttributeSelectionComponent component = new AttributeSelectionComponent(detector);
        component.open();
        component.requestVisible();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (detector != null) {
            setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new PerformAttributeSelection(actionContext);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends Detector> allInstances = result.allInstances();
        if (allInstances.isEmpty()) {
            setEnabled(false);
        } else {
            Detector next = result.allInstances().iterator().next();
            if (!next.equals(this.detector)) {
                this.detector = next;
                this.detector.addPropertyChangeListener(WeakListeners.propertyChange(PerformAttributeSelection.this, detector));
                setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
            }
        }
    }
}
