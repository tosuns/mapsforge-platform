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
package de.fub.maps.project.detector.ui;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.inference.AbstractInferenceModel;
import de.fub.maps.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.maps.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.components.CustomListView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class InferenceModelComponent extends javax.swing.JPanel implements ExplorerManager.Provider, PropertyChangeListener, ActionListener {

    private static final long serialVersionUID = 1L;
    private Detector detector;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private final ArrayList<AbstractInferenceModel> registeredModels = new ArrayList<AbstractInferenceModel>();
    private static BufferedImage EMPTY_IMAGE = null;

    /**
     * Creates new form InferenceModelComponent
     */
    public InferenceModelComponent() {
        initComponents();
        listView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getExplorerManager().addPropertyChangeListener(InferenceModelComponent.this);
        applyButton.addActionListener(InferenceModelComponent.this);
    }

    public InferenceModelComponent(Lookup lookup) {
        this();
        detector = lookup.lookup(Detector.class);
        assert detector != null;
        init();
    }

    private void init() {
        AbstractInferenceModel currentInferenceModel = detector.getInferenceModel();
        Set<Class<? extends AbstractInferenceModel>> allClasses = Lookup.getDefault().lookupResult(AbstractInferenceModel.class).allClasses();

        for (Class<? extends AbstractInferenceModel> modelClass : allClasses) {
            AbstractInferenceModel inferenceModel = DetectorUtils.createInstance(modelClass, modelClass.getName());
            if (inferenceModel != null) {
                if (currentInferenceModel != null) {
                    if (inferenceModel.getClass().equals(currentInferenceModel.getClass())) {
                        registeredModels.add(currentInferenceModel);
                    } else {
                        registeredModels.add(inferenceModel);
                    }
                } else {
                    registeredModels.add(inferenceModel);
                }
            }
        }

        getExplorerManager().setRootContext(new AbstractNode(Children.create(new InferenceModelNodeFactory(registeredModels), true)));

        initializeSelection();
    }

    private void initializeSelection() {
        Node[] nodes = getExplorerManager().getRootContext().getChildren().getNodes(true);
        AbstractInferenceModel inferenceModel = detector.getInferenceModel();
        Node selectedNode = null;
        if (inferenceModel != null) {
            for (Node node : nodes) {
                AbstractInferenceModel abstractInferenceModel = node.getLookup().lookup(AbstractInferenceModel.class);
                if (abstractInferenceModel != null && abstractInferenceModel.equals(inferenceModel)) {
                    selectedNode = node;
                    selectedInferenceModel.setText(abstractInferenceModel.getName());
                    break;
                }
            }
            if (selectedNode != null) {
                try {
                    getExplorerManager().setSelectedNodes(new Node[]{selectedNode});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
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

        jLabel1 = new javax.swing.JLabel();
        listView = new CustomListView();
        jScrollPane1 = new javax.swing.JScrollPane();
        settingsPanelContainer = new javax.swing.JPanel();
        applyButton = new javax.swing.JButton();
        selectedInferenceModel = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InferenceModelComponent.class, "InferenceModelComponent.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        jLabel1.setMaximumSize(new java.awt.Dimension(34, 32));
        jLabel1.setPreferredSize(new java.awt.Dimension(34, 32));

        listView.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        settingsPanelContainer.setPreferredSize(new java.awt.Dimension(390, 240));
        settingsPanelContainer.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(settingsPanelContainer);

        org.openide.awt.Mnemonics.setLocalizedText(applyButton, org.openide.util.NbBundle.getMessage(InferenceModelComponent.class, "InferenceModelComponent.applyButton.text")); // NOI18N
        applyButton.setActionCommand(org.openide.util.NbBundle.getMessage(InferenceModelComponent.class, "InferenceModelComponent.applyButton.actionCommand")); // NOI18N
        applyButton.setEnabled(false);

        selectedInferenceModel.setEditable(false);
        selectedInferenceModel.setText(org.openide.util.NbBundle.getMessage(InferenceModelComponent.class, "InferenceModelComponent.selectedInferenceModel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(InferenceModelComponent.class, "InferenceModelComponent.jLabel2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(listView, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectedInferenceModel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(applyButton))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(applyButton)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(selectedInferenceModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))))
                    .addComponent(listView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.openide.explorer.view.ListView listView;
    private javax.swing.JTextField selectedInferenceModel;
    private javax.swing.JPanel settingsPanelContainer;
    // End of variables declaration//GEN-END:variables

    @Override
    public final ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            settingsPanelContainer.removeAll();
            Node[] selectedNodes = getExplorerManager().getSelectedNodes();
            if (selectedNodes.length == 1) {
                AbstractInferenceModel abstractInferenceModel = selectedNodes[0].getLookup().lookup(AbstractInferenceModel.class);
                if (abstractInferenceModel != null) {
                    JComponent visualRepresenter = abstractInferenceModel.getSettingsView();
                    if (visualRepresenter != null) {
                        settingsPanelContainer.add(visualRepresenter, BorderLayout.CENTER);
                    }
                }
                applyButton.setEnabled(true);
            } else {
                applyButton.setEnabled(false);
            }
            settingsPanelContainer.revalidate();
            repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Node[] selectedNodes = getExplorerManager().getSelectedNodes();
        if (selectedNodes.length == 1) {
            AbstractInferenceModel abstractInferenceModel = selectedNodes[0].getLookup().lookup(AbstractInferenceModel.class);
            if (abstractInferenceModel != null) {
                InferenceModelDescriptor inferenceModelDescriptor = abstractInferenceModel.getInferenceModelDescriptor();
                if (inferenceModelDescriptor != null) {
                    selectedInferenceModel.setText(abstractInferenceModel.getName());
                    detector.getDetectorDescriptor().setInferenceModel(inferenceModelDescriptor);
                } else {
                    throw new IllegalStateException("Inferene model doesn't have a inferenceModelDescriptor!");
                }
            } else {
                throw new IllegalStateException("Node doesn't contain an inference model");
            }
        }
    }

    private static class InferenceModelNodeFactory extends ChildFactory<AbstractInferenceModel> {

        private final List<AbstractInferenceModel> registeredInferenceModels;

        public InferenceModelNodeFactory(List<AbstractInferenceModel> inferenceModels) {
            this.registeredInferenceModels = inferenceModels;
        }

        @Override
        protected boolean createKeys(List<AbstractInferenceModel> toPopulate) {
            toPopulate.addAll(registeredInferenceModels);
            Collections.sort(toPopulate);
            return true;
        }

        @Override
        protected Node createNodeForKey(AbstractInferenceModel inferenceModel) {
            return new InferenceModelFilterNode(inferenceModel.getNodeDelegate());
        }
    }

    private static class InferenceModelFilterNode extends FilterNode {

        public InferenceModelFilterNode(Node original) {
            super(original, Children.LEAF);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }

        @Override
        public Image getIcon(int type) {
            return getEmptyImage();
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type); //To change body of generated methods, choose Tools | Templates.
        }

        private static BufferedImage getEmptyImage() {
            if (EMPTY_IMAGE == null) {
                EMPTY_IMAGE = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g2d = EMPTY_IMAGE.createGraphics();
                g2d.setPaint(new Color(1, 1, 1, 0));
                g2d.fill(new Rectangle(EMPTY_IMAGE.getWidth(), EMPTY_IMAGE.getHeight()));
                g2d.dispose();
            }
            return EMPTY_IMAGE;
        }
    }
}
