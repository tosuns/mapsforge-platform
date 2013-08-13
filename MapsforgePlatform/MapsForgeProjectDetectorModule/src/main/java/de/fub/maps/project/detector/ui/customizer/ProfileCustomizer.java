/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.ui.customizer;

import de.fub.maps.project.detector.ui.ProfileComponent;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class ProfileCustomizer implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GENERAL = "Profiles";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = "org-maps-project/Detector", position = 400)
    public static ProfileCustomizer createGeneral() {
        return new ProfileCustomizer();
    }

    @NbBundle.Messages("LBL_Config_Profile=Profiles")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                GENERAL,
                Bundle.LBL_Config_Profile(),
                null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        return new ProfileComponent(context);
    }
}
