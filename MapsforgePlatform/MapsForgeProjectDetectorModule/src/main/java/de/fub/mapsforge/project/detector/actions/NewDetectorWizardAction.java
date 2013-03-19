/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.wizards.detector.CommonDetectorInformationWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.InferenceDataSetSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.InferenceModelFeatureSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.InferenceModelSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.PostprocessorSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.PreprocessorSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.TrainingSetSelectionWizardPanel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Detector", id = "de.fub.mapsforge.project.detector.actions.NewDetectorWizardAction")
@ActionRegistration(displayName = "#CLT_NewDetectorWizardAction_Name")
@ActionReference(path = "Projects/org-mapsforge-project/Detector/Actions", position = 100)
@NbBundle.Messages({
    "CLT_NewDetectorWizardAction_Name=New Detector...",
    "CLT_New_Detector_Wizard_Title=New Detector Wizard"
})
public final class NewDetectorWizardAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        WizardDescriptor wiz = new WizardDescriptor(new DetectorWizardIterator());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(Bundle.CLT_New_Detector_Wizard_Title());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            // do something
        }
    }

    private static class DetectorWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

        private int index;
        private List<WizardDescriptor.Panel<WizardDescriptor>> panels;

        private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
            if (panels == null) {
                panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
                panels.add(new CommonDetectorInformationWizardPanel());
                panels.add(new InferenceModelSelectionWizardPanel());
                panels.add(new InferenceModelFeatureSelectionWizardPanel());
                panels.add(new PreprocessorSelectionWizardPanel());
                panels.add(new PostprocessorSelectionWizardPanel());
                panels.add(new TrainingSetSelectionWizardPanel());
                panels.add(new InferenceDataSetSelectionWizardPanel());
                String[] steps = new String[panels.size()];
                for (int i = 0; i < panels.size(); i++) {
                    Component c = panels.get(i).getComponent();
                    // Default step name to component name of panel.
                    steps[i] = c.getName();
                    if (c instanceof JComponent) { // assume Swing components
                        JComponent jc = (JComponent) c;
                        jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                        jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                        jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                        jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                        jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                    }
                }
            }
            return panels;
        }

        @Override
        public WizardDescriptor.Panel<WizardDescriptor> current() {
            return getPanels().get(index);
        }

        @Override
        public String name() {
            return index + 1 + ". from " + getPanels().size();
        }

        @Override
        public boolean hasNext() {
            return index < getPanels().size() - 1;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public void nextPanel() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index++;
        }

        @Override
        public void previousPanel() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            index--;
        }

        // If nothing unusual changes in the middle of the wizard, simply:
        @Override
        public void addChangeListener(ChangeListener l) {
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
        }
        // If something changes dynamically (besides moving between panels), e.g.
        // the number of panels changes in response to user input, then use
        // ChangeSupport to implement add/removeChangeListener and call fireChange
        // when needed
    }
}
