/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.impl;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import weka.classifiers.Classifier;
import weka.core.Drawable;
import weka.gui.treevisualizer.Node;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeBuild;
import weka.gui.treevisualizer.TreeVisualizer;

/**
 *
 * @author Serdar
 */
class ShowGraphAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final AbstractInferenceModel inferenceModel;
    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/detector/model/inference/ui/binaryTreeIcon.png";

    @NbBundle.Messages({
        "CLT_ShowGraphAction_Description=Displays the graph of the used classifiey, if it supports the creation of a graph."
    })
    public ShowGraphAction(AbstractInferenceModel inferenceModel) {
        super(null, ImageUtilities.loadImageIcon(ICON_PATH, false));
        putValue(Action.SHORT_DESCRIPTION, Bundle.CLT_ShowGraphAction_Description());
        assert inferenceModel != null;
        this.inferenceModel = inferenceModel;
        setEnabled(this.inferenceModel.getClassifier() instanceof Drawable);
    }

    @NbBundle.Messages({
        "CLT_Tree_Visualization=Tree Visualisation"
    })
    @Override
    public void actionPerformed(ActionEvent e) {
        Classifier classifier = inferenceModel.getClassifier();
        if (classifier instanceof Drawable) {
            try {
                Drawable graph = (Drawable) classifier;
                JComponent panel = null;
                switch (graph.graphType()) {
                    case Drawable.BayesNet:
                        break;
                    case Drawable.Newick:
                        break;
                    case Drawable.TREE:
                        String graphDescriptor = graph.graph();
                        if (graphDescriptor != null) {
                            TreeBuild builder = new TreeBuild();
                            NodePlace placeNode = new PlaceNode2();
                            Node node = builder.create(new StringReader(graphDescriptor));
                            panel = new TreeVisualizer(null, node, placeNode);
                        }
                        break;
                    default:
                        break;
                }
                if (panel != null) {
                    panel.setPreferredSize(new Dimension(800, 600));
                    DialogDescriptor dd = new DialogDescriptor(panel, Bundle.CLT_Tree_Visualization());
                    Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
                    dialog.setVisible(true);
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
