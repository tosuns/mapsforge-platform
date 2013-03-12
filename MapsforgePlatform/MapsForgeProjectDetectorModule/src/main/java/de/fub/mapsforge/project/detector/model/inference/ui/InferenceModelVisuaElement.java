/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui;

import de.fub.mapsforge.project.detector.filetype.DetectorDataObject;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.mapsforge.project.detector.model.inference.ui.actions.SaveAsHtmlAction;
import de.fub.mapsforge.project.detector.model.inference.ui.actions.SaveAsPdfAction;
import de.fub.mapsforge.project.detector.model.inference.ui.actions.SaveAsSvgAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.Toolbar;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
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
@NbBundle.Messages({
    "LBL_Detector_InferenceModel_VISUAL=Evaluation",})
public class InferenceModelVisuaElement extends javax.swing.JPanel implements MultiViewElement {

    private static final long serialVersionUID = 1L;
    private final DetectorDataObject obj;
    private final JToolBar toolbar = new JToolBar();
    private AbstractInferenceModel inferenceModel;
    private Detector detector;
    private MultiViewElementCallback callback;

    /**
     * Creates new form InferenceModelVisuaElement
     */
    public InferenceModelVisuaElement(Lookup lkp) {
        obj = lkp.lookup(DetectorDataObject.class);
        assert obj != null;
        initComponents();
        addToolbarActions();
        init();
    }

    private void init() {
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(8);
        detector = obj.getNodeDelegate().getLookup().lookup(Detector.class);
        if (detector != null) {
            inferenceModel = detector.getInferenceModel();
            if (inferenceModel != null) {
                contentPanel.add(inferenceModel.getProcessHandlerInstance(InferenceMode.TRAININGS_MODE).getVisualRepresentation());
                contentPanel.add(Box.createVerticalStrut(32));
                contentPanel.add(inferenceModel.getProcessHandlerInstance(InferenceMode.CROSS_VALIDATION_MODE).getVisualRepresentation());
                contentPanel.add(Box.createVerticalStrut(32));
                contentPanel.add(inferenceModel.getProcessHandlerInstance(InferenceMode.INFERENCE_MODE).getVisualRepresentation());
                contentPanel.add(Box.createVerticalStrut(32));
            }
        }
    }

    @Override
    public String getName() {
        return "InferenceModelVisuaElement";
    }

    private void addToolbarActions() {
        Action[] actions = new Action[]{new SaveAsPdfAction(contentPanel), new SaveAsSvgAction(contentPanel), new SaveAsHtmlAction(contentPanel)};
        toolbar.add(new Toolbar.Separator());
        for (Action action : actions) {
            toolbar.add(action);
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
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return obj.getLookup();
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
}
