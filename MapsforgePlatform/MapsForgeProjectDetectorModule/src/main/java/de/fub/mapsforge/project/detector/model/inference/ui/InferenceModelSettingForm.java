/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui;

import de.fub.mapsforge.project.detector.model.DetectorProcess;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import org.openide.DialogDescriptor;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class InferenceModelSettingForm extends javax.swing.JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final AbstractInferenceModel inferenecModel;

    /**
     * Creates new form InferenceModelSettingForm
     */
    public InferenceModelSettingForm() {
        this(null);
    }

    public InferenceModelSettingForm(AbstractInferenceModel inferenceModel) {
        assert inferenceModel != null;
        initComponents();

        this.inferenecModel = inferenceModel;

        selectionComponent1.getAllItemListTitle().setText("All Features"); //NO18N
        selectionComponent1.getSelectedItemListTitle().setText("Selected Features"); // NO18N

        selectionComponent1.getAllItems().addAll(Lookup.getDefault().lookupResult(FeatureProcess.class).allInstances());
        Collection<FeatureProcess> featureList = inferenceModel.getFeatureList();

        for (DetectorProcess feature : featureList) {
            for (DetectorProcess f : selectionComponent1.getAllItems()) {
                if (feature.getClass().equals(f.getClass())) {
                    selectionComponent1.getSelectedItems().add(f);
                }
            }
        }
        selectionComponent1.getAllItems().removeAll(selectionComponent1.getSelectedItems());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        optionPanel1 = new de.fub.mapsforge.project.detector.model.inference.ui.OptionPanel();
        selectionComponent1 = new de.fub.mapsforge.project.detector.ui.SelectionComponent();
        jPanel1 = new javax.swing.JPanel();
        processHandlerPanel1 = new de.fub.mapsforge.project.detector.model.inference.ui.ProcessHandlerPanel();

        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(700, 450));
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(InferenceModelSettingForm.class, "InferenceModelSettingForm.optionPanel1.TabConstraints.tabTitle"), optionPanel1); // NOI18N

        selectionComponent1.setPreferredSize(new java.awt.Dimension(400, 450));
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(InferenceModelSettingForm.class, "InferenceModelSettingForm.selectionComponent1.TabConstraints.tabTitle"), selectionComponent1); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(processHandlerPanel1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(InferenceModelSettingForm.class, "InferenceModelSettingForm.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private de.fub.mapsforge.project.detector.model.inference.ui.OptionPanel optionPanel1;
    private de.fub.mapsforge.project.detector.model.inference.ui.ProcessHandlerPanel processHandlerPanel1;
    private de.fub.mapsforge.project.detector.ui.SelectionComponent selectionComponent1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(DialogDescriptor.OK_OPTION)) {
        } else if (e.getSource().equals(DialogDescriptor.CANCEL_OPTION)) {
        }
    }
}
