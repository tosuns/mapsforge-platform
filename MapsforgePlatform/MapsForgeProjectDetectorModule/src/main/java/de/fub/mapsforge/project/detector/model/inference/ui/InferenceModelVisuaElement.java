/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui;

import de.fub.mapsforge.project.detector.filetype.DetectorDataObject;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.List;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.Toolbar;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Serdar
 */
@MultiViewElement.Registration(
        displayName = "#LBL_Detector_InferenceModel_VISUAL",
        iconBase = "de/fub/mapsforge/project/detector/model/inference/inferenceModelIcon.png",
        mimeType = "text/detector+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "DetectorInferenceModelVisual",
        position = 3000)
@ActionReferences({
    @ActionReference(
            id =
            @ActionID(
            category = "SnapShot",
            id = "de.fub.mapsforge.snapshot.api.SnapShotExporterDelegateAction"),
            path = "Mapsforge/Gpx/MapView/Actions",
            position = 250,
            separatorAfter = 275)})
@NbBundle.Messages({
    "LBL_Detector_InferenceModel_VISUAL=Evaluation",})
public class InferenceModelVisuaElement extends javax.swing.JPanel implements MultiViewElement, ChangeListener {

    private static final long serialVersionUID = 1L;
    private final DetectorDataObject obj;
    private final JToolBar toolbar = new JToolBar();
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private AbstractInferenceModel inferenceModel;
    private Detector detector;
    private MultiViewElementCallback callback;
    private Lookup lookup;

    /**
     * Creates new form InferenceModelVisuaElement
     */
    public InferenceModelVisuaElement(Lookup lkp) {
        initComponents();
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(8);
        addToolbarActions();
        obj = lkp.lookup(DetectorDataObject.class);
        assert obj != null;

        lookup = new ProxyLookup(lkp, Lookups.singleton(contentPanel));

        detector = obj.getNodeDelegate().getLookup().lookup(Detector.class);
        assert detector != null;
        modelSynchronizerClient = detector.create(InferenceModelVisuaElement.this);
        reinit();
    }

    private void reinit() {
        contentPanel.removeAll();

        if (inferenceModel != null && inferenceModel.getToolbarRepresenter() != null) {
            toolbar.remove(inferenceModel.getToolbarRepresenter());
        }
        inferenceModel = detector.getInferenceModel();

        if (inferenceModel != null) {
            if (inferenceModel.getToolbarRepresenter() != null) {
                toolbar.add(inferenceModel.getToolbarRepresenter());
                toolbar.revalidate();
            }
            contentPanel.add(inferenceModel.getProcessHandlerInstance(InferenceMode.TRAININGS_MODE).getVisualRepresentation());
            contentPanel.add(Box.createVerticalStrut(32));
            contentPanel.add(inferenceModel.getProcessHandlerInstance(InferenceMode.CROSS_VALIDATION_MODE).getVisualRepresentation());
            contentPanel.add(Box.createVerticalStrut(32));
            contentPanel.add(inferenceModel.getProcessHandlerInstance(InferenceMode.INFERENCE_MODE).getVisualRepresentation());
            contentPanel.add(Box.createVerticalStrut(32));
            contentPanel.revalidate();
            repaint();
        }

    }

    @Override
    public String getName() {
        return "InferenceModelVisuaElement";
    }

    private void addToolbarActions() {
        toolbar.setFloatable(false);
        toolbar.add(new JToolBar.Separator());
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Mapsforge/Gpx/MapView/Actions");
        for (Action action : actionsForPath) {
            if (action instanceof Presenter.Toolbar) {
                Presenter.Toolbar presenter = (Presenter.Toolbar) action;
                toolbar.add(presenter.getToolbarPresenter());
            } else if (action == null) {
                toolbar.add(new Toolbar.Separator());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(contentPanel);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[]{};
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                reinit();
            }
        });
    }
}
