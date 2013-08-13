/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.ui.customizer;

import de.fub.maps.project.detector.ui.TrainingDatasetComponent;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class TrainingsSetCustomizer implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GENERAL = "Training Dataset";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = "org-maps-project/Detector", position = 100)
    public static TrainingsSetCustomizer createGeneral() {
        return new TrainingsSetCustomizer();
    }

    @NbBundle.Messages("LBL_Config_trainingsset=Training Dataset")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                GENERAL,
                Bundle.LBL_Config_trainingsset(),
                null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        return new TrainingDatasetComponent(context);
    }
}
