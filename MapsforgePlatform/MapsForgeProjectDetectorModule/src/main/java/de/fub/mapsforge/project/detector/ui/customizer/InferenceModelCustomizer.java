/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.ui.customizer;

import de.fub.mapsforge.project.detector.ui.InferenceModelComponent;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class InferenceModelCustomizer implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GENERAL = "Inference Models";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = "org-mapsforge-project/Detector", position = 10)
    public static InferenceModelCustomizer createGeneral() {
        return new InferenceModelCustomizer();
    }

    @NbBundle.Messages("LBL_Config_inferenceModule=Inference Models")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                GENERAL,
                Bundle.LBL_Config_inferenceModule(),
                null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        return new InferenceModelComponent(context);
    }
}
