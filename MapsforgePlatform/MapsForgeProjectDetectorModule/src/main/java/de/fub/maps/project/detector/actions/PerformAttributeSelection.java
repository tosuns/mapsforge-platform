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
import de.fub.maps.project.detector.model.inference.ui.AttributeSelectionComponent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
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
        id = "de.fub.maps.project.detector.model.inference.actions.PerformAttributeSelection")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_PerformAttributeSelection")
@ActionReferences(
        @ActionReference(
                id
                = @ActionID(
                        category = "Detector",
                        id = "de.fub.maps.project.detector.model.inference.actions.PerformAttributeSelection"),
                path = "Loaders/text/detector+xml/Actions", position = 255))
@Messages({"CTL_PerformAttributeSelection=Perform Attribute Selection",
    "CLT_PerformAttributeSelection_Dialog_Title=Run Attribute Selection"
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
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setEnabled(detector.getDetectorState() != ProcessState.RUNNING);
                }
            });

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
