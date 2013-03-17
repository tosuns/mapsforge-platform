/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.impl;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
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
@NbBundle.Messages({
    "CLT_J48InferenceModel_Name=J48 Inference Model",
    "CLT_J48InferenceModel_Description=Inference Model which uses a J48 classifier that generates an unpruned or a pruned C4.5 decision tree."
})
@ServiceProvider(service = AbstractInferenceModel.class)
public class J48InferenceModel extends AbstractInferenceModel {

    private final JToolBar toolbar = new JToolBar();

    public J48InferenceModel() {
        this(null);
    }

    public J48InferenceModel(Detector detector) {
        super(detector);
        initToolBar();
    }

    private void initToolBar() {
        toolbar.setFloatable(false);
        toolbar.add(new JButton(new ShowGraphAction(J48InferenceModel.this)));
    }

    @Override
    public JToolBar getToolbarRepresenter() {
        return toolbar;
    }

    @Override
    protected Classifier createClassifier() {
        return new J48();
    }

    @Override
    public String getName() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_J48InferenceModel_Name();
    }

    @Override
    public String getDescription() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getDescription();
        }
        return Bundle.CLT_J48InferenceModel_Description();
    }

    private static class ShowGraphAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final J48InferenceModel inferenceModel;
        @StaticResource
        private static final String ICON_PATH = "de/fub/mapsforge/project/detector/model/inference/ui/binaryTreeIcon.png";

        @NbBundle.Messages({
            "CLT_ShowGraphAction_Description=Displays the graph of the used classifiey, if it supports the creation of a graph."
        })
        public ShowGraphAction(J48InferenceModel inferenceModel) {
            super(null, ImageUtilities.loadImageIcon(ICON_PATH, false));
            putValue(Action.SHORT_DESCRIPTION, Bundle.CLT_ShowGraphAction_Description());
            assert inferenceModel != null;
            this.inferenceModel = inferenceModel;
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
}
