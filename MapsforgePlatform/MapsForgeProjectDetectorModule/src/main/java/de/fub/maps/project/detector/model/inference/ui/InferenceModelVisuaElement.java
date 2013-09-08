/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.model.inference.ui;

import de.fub.maps.project.detector.filetype.DetectorDataObject;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.inference.AbstractInferenceModel;
import de.fub.maps.project.detector.model.inference.actions.ToolbarDetectorStartAction;
import de.fub.maps.project.detector.model.inference.processhandler.InferenceModelProcessHandler;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.ArrayList;
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
import org.openide.awt.Toolbar;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Serdar
 */
@MultiViewElement.Registration(
        displayName = "#LBL_Detector_InferenceModel_VISUAL",
        iconBase = "de/fub/maps/project/detector/model/inference/inferenceModelIcon.png",
        mimeType = "text/detector+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "DetectorInferenceModelVisual",
        position = 1000)
@NbBundle.Messages({
    "LBL_Detector_InferenceModel_VISUAL=Evaluation",})
public class InferenceModelVisuaElement extends javax.swing.JPanel implements MultiViewElement, ChangeListener {

    private static final long serialVersionUID = 1L;
    private final DetectorDataObject obj;
    private final JToolBar toolbar = new JToolBar();
    // This tool bar is a proxy toolbar. we need this one for the cases the detector and  its inference model get modified
    // an the successive re init of the toolbar. we make sure the inference model
    // tool bar representer will always be at the end of this element toolbar
    private final JToolBar inferenceModelProxyToolbar = new JToolBar();
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private AbstractInferenceModel inferenceModel;
    private final Detector detector;
    private MultiViewElementCallback callback;
    private final Lookup lookup;

    /**
     * Creates new form InferenceModelVisuaElement
     *
     * @param lkp
     */
    public InferenceModelVisuaElement(Lookup lkp) {
        initComponents();
        inferenceModelProxyToolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(8);
        obj = lkp.lookup(DetectorDataObject.class);
        assert obj != null;
        detector = obj.getNodeDelegate().getLookup().lookup(Detector.class);
        lookup = new ProxyLookup(lkp, Lookups.fixed(detector, contentPanel));
        assert detector != null;
        modelSynchronizerClient = detector.create(InferenceModelVisuaElement.this);

        addToolbarActions();
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
            for (InferenceModelProcessHandler handler : inferenceModel.getProcessHandlers()) {
                contentPanel.add(handler.getVisualRepresentation());
                contentPanel.add(Box.createVerticalStrut(16));
            }
            contentPanel.revalidate();
            repaint();
        }

    }

    @Override
    public String getName() {
        return "InferenceModelVisuaElement";
    }

    private void addToolbarActions() {
        inferenceModelProxyToolbar.setFloatable(false);
        toolbar.setFloatable(false);
        // toolbar seperator
        toolbar.add(new JToolBar.Separator());

        // get all register actions from the respective folder
        List<Action> actionsForPath = new ArrayList<Action>();
        actionsForPath.addAll(Utilities.actionsForPath("Mapsforge/Gpx/MapView/Actions"));
        for (Action action : actionsForPath) {
            if (action == null) {
                toolbar.add(new Toolbar.Separator());
            } else {
                toolbar.add(action);
            }
        }
        toolbar.add(new ToolbarDetectorStartAction(detector).getToolbarPresenter());
        toolbar.add(new JToolBar.Separator());
//        toolbar.add(inferenceModelProxyToolbar);
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
        contentPanel.setMaximumSize(new java.awt.Dimension(0, 35635));
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
        Action[] retValue;
        // the multiviewObserver was passed to the element in setMultiViewCallback() method.
        if (callback != null) {
            retValue = callback.createDefaultActions();
            // add you own custom actions here..
        } else {
            // fallback..
            retValue = new Action[0];
        }
        return retValue;
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
        String displayNString = MessageFormat.format("{0}[{1}]",
                detector.getDataObject().getName(),
                detector.getInferenceModel().getName());
        TopComponent topComponent = callback.getTopComponent();
        topComponent.setDisplayName(displayNString);
        topComponent.setHtmlDisplayName(displayNString);
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
