/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.mapsforge.project.detector.utils.DetectorUtils.DetectorCopyException;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.ShowCustomizerAction")
@ActionRegistration(
        displayName = "#CTL_ShowCustomizerAction")
@ActionReference(path = "Loaders/text/detector+xml/Actions", position = 1300, separatorAfter = 1350)
@Messages("CTL_ShowCustomizerAction=Settings")
public final class ShowCustomizerAction implements ActionListener {

    private final Detector detectorOriginalInstance;
    public static final String PROJECT_FOLDER = "Projects/org-mapsforge-project/Detector/Customizer";
    private Detector detectorCopyInstance;

    public ShowCustomizerAction(Detector context) {
        this.detectorOriginalInstance = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            detectorCopyInstance = DetectorUtils.copyInstance(detectorOriginalInstance);
            Dialog createCustomizerDialog;
            createCustomizerDialog = ProjectCustomizer.createCustomizerDialog(PROJECT_FOLDER, Lookups.fixed(detectorCopyInstance), "", new OKButtonListener(), null);
            createCustomizerDialog.setModal(true);
            createCustomizerDialog.setVisible(true);
        } catch (DetectorCopyException ex) {
            NotifyDescriptor.Message nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.Message.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    class OKButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (detectorOriginalInstance != null) {
                DetectorUtils.mergeDetector(detectorOriginalInstance, detectorCopyInstance);
            }
        }
    }
}
