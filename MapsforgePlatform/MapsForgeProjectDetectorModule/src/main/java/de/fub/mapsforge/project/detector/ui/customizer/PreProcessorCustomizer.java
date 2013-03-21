/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.ui.customizer;

import de.fub.mapsforge.project.detector.ui.PreprocessorComponent;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class PreProcessorCustomizer implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GENERAL = "Preprocessors";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = "org-mapsforge-project/Detector", position = 200)
    public static PreProcessorCustomizer createGeneral() {
        return new PreProcessorCustomizer();
    }

    @NbBundle.Messages("LBL_Config_Preprocessors=Preprocessors")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                GENERAL,
                Bundle.LBL_Config_Preprocessors(),
                null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        return new PreprocessorComponent(context);
    }
}
