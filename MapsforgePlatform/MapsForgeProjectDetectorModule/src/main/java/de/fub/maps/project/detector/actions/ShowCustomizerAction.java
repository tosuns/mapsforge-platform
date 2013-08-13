/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.actions;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.utils.DetectorUtils;
import de.fub.maps.project.detector.utils.DetectorUtils.DetectorCopyException;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.actions.ShowCustomizerAction")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_ShowCustomizerAction")
@ActionReference(path = "Loaders/text/detector+xml/Actions", position = 1300, separatorAfter = 1350)
@Messages("CTL_ShowCustomizerAction=Settings")
public final class ShowCustomizerAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private static final long serialVersionUID = 1L;
    private Detector detectorOriginalInstance;
    public static final String PROJECT_FOLDER = "Projects/org-maps-project/Detector/Customizer";
    private Detector detectorCopyInstance;
    private Lookup.Result<Detector> result;

    public ShowCustomizerAction() {
        super(Bundle.CTL_ShowCustomizerAction());
    }

    public ShowCustomizerAction(Lookup context) {
        this();
        result = context.lookupResult(Detector.class);
        result.addLookupListener(ShowCustomizerAction.this);
        resultChanged(new LookupEvent(result));
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

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new ShowCustomizerAction(actionContext);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends Detector> allInstances = result.allInstances();
        if (allInstances.isEmpty()) {
            setEnabled(false);
        } else {
            this.detectorOriginalInstance = allInstances.iterator().next();
            setEnabled(true);
        }
    }

    class OKButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (detectorOriginalInstance != null) {
                        DetectorUtils.mergeDetector(detectorOriginalInstance, detectorCopyInstance);
                        FileUtil.refreshFor(FileUtil.toFile(detectorOriginalInstance.getDataObject().getPrimaryFile()));
                    }
                    FileObject primaryFile = detectorCopyInstance.getDataObject().getPrimaryFile();
                    File file = FileUtil.toFile(primaryFile);
                    if (file != null) {
                        file.delete();
                    }
                }
            });

        }
    }
}
