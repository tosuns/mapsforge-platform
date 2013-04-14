/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.ui.AttributeSelectionComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.model.inference.actions.PerformAttributeSelection")
@ActionRegistration(
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
public final class PerformAttributeSelection implements ActionListener {

    private final Detector detector;

    public PerformAttributeSelection(Detector context) {
        this.detector = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        AttributeSelectionComponent component = new AttributeSelectionComponent(detector);
        DialogDescriptor dd = new DialogDescriptor(component, Bundle.CLT_PerformAttributeSelection_Dialog_Title());
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
    }
}
