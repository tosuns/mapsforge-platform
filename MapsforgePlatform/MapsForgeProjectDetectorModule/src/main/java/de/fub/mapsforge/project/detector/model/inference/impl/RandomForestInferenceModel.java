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
import weka.classifiers.trees.RandomForest;
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
    "CLT_RandomForestInferenceModel_Name=Random Forest Inference Model",
    "CLT_RandomForestInferenceModel_Description=Inference Model which uses a RandomForest Classifier"
})
@ServiceProvider(service = AbstractInferenceModel.class)
public class RandomForestInferenceModel extends AbstractInferenceModel {

    private JToolBar toolbar = null;
    private RandomForest classifier = null;

    public RandomForestInferenceModel() {
    }

    public RandomForestInferenceModel(Detector detector) {
        super(detector);
    }

    private void initToolBar() {
        toolbar.setFloatable(false);
        toolbar.add(new JButton(new ShowGraphAction(RandomForestInferenceModel.this)));
    }

    @Override
    public JToolBar getToolbarRepresenter() {
        if (toolbar == null) {
            toolbar = new JToolBar();
            initToolBar();
        }
        return toolbar;
    }

    @Override
    protected Classifier createClassifier() {
        classifier = new RandomForest();
        return classifier;
    }

    @Override
    public String getName() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_RandomForestInferenceModel_Name();
    }

    @Override
    public String getDescription() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_RandomForestInferenceModel_Description();
    }

    private static class ShowGraphAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final RandomForestInferenceModel inferenceModel;
        @StaticResource
        private static final String ICON_PATH = "de/fub/mapsforge/project/detector/model/inference/ui/binaryTreeIcon.png";

        public ShowGraphAction(RandomForestInferenceModel inferenceModel) {
            super(null, ImageUtilities.loadImageIcon(ICON_PATH, false));
            putValue(Action.SHORT_DESCRIPTION, Bundle.CLT_ShowGraphAction_Description());
            assert inferenceModel != null;
            this.inferenceModel = inferenceModel;
            setEnabled(inferenceModel.getClassifier() instanceof Drawable);
        }

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
