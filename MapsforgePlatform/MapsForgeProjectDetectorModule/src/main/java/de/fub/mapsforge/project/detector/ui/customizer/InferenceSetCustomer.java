/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.ui.customizer;

import de.fub.mapsforge.project.detector.ui.InferenceDatasetComponent;
import de.fub.mapsforge.project.detector.ui.TrainingDatasetComponent;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class InferenceSetCustomer implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GENERAL = "Inference Dataset";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = "org-mapsforge-project/Detector", position = 100)
    public static InferenceSetCustomer createGeneral() {
        return new InferenceSetCustomer();
    }

    @NbBundle.Messages("LBL_Config_inferenceSet=Inference Dataset")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                GENERAL,
                Bundle.LBL_Config_inferenceSet(),
                null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        return new InferenceDatasetComponent(context);
    }
}
