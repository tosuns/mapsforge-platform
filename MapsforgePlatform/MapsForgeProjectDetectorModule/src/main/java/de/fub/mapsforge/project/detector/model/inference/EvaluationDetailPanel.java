/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import java.beans.IntrospectionException;
import java.beans.PropertyVetoException;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import weka.classifiers.Evaluation;

/**
 *
 * @author Serdar
 */
public class EvaluationDetailPanel extends javax.swing.JPanel implements ExplorerManager.Provider {

    private static final long serialVersionUID = 1L;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private Evaluation evaluation;

    /**
     * Creates new form EvaluationDetailPanel
     */
    public EvaluationDetailPanel() {
        initComponents();
    }

    public EvaluationDetailPanel(Evaluation evaluation) {
        this();
        setEvaluation(evaluation);
    }

    public final void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
        try {
            getExplorerManager().setRootContext(new DetailEvaluationNode(evaluation));
            getExplorerManager().setSelectedNodes(new Node[]{getExplorerManager().getRootContext()});
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
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

        propertySheetView1 = new org.openide.explorer.propertysheet.PropertySheetView();

        setLayout(new java.awt.BorderLayout());

        propertySheetView1.setPreferredSize(new java.awt.Dimension(350, 600));
        add(propertySheetView1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.propertysheet.PropertySheetView propertySheetView1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private static class DetailEvaluationNode extends BeanNode<EvaluationBean> {

        public DetailEvaluationNode(Evaluation evaluation) throws IntrospectionException {
            super(new EvaluationBean(evaluation), Children.LEAF);
        }
    }
}
