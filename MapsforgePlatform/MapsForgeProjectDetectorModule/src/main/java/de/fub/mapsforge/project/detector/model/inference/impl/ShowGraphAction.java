/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.impl;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import weka.classifiers.Classifier;
import weka.core.Drawable;
import weka.gui.graphvisualizer.BIFFormatException;
import weka.gui.graphvisualizer.GraphVisualizer;
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

    private static final Logger LOG = Logger.getLogger(ShowGraphAction.class.getName());
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
        "CLT_Tree_Visualization=Visualisation"
    })
    @Override
    public void actionPerformed(ActionEvent e) {
        Classifier classifier = inferenceModel.getClassifier();
        if (classifier instanceof Drawable) {
            try {
                Drawable graph = (Drawable) classifier;
                JComponent panel = null;
                String graphDescriptor = null;

                switch (graph.graphType()) {
                    case Drawable.BayesNet:
                        graphDescriptor = graph.graph();

                        if (graphDescriptor != null) {
                            LOG.log(Level.INFO, "\n{0}", graphDescriptor);
                            panel = createBayesGraph(graphDescriptor);
                        }
                        break;
                    case Drawable.Newick:
                        break;
                    case Drawable.TREE:
                        graphDescriptor = graph.graph();
                        if (graphDescriptor != null) {
                            LOG.log(Level.INFO, "\n{0}", graphDescriptor);
                            panel = createTreeGraph(graphDescriptor);
                        }
                        break;
                    default:
                        break;
                }

                if (panel != null) {
                    displayGraph(panel);
                }
            } catch (Exception ex) {
                NotifyDescriptor nd = new NotifyDescriptor.Message(
                        MessageFormat.format(
                        "{0}\nYou have to build the classifier before you can visualize.",
                        ex.getMessage()),
                        NotifyDescriptor.Message.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        }
    }

    private JComponent createBayesGraph(String graphDescriptor) throws BIFFormatException {
        GraphVisualizer graphVisualizer = new GraphVisualizer();
        graphVisualizer.readBIF(graphDescriptor);
        graphVisualizer.layoutGraph();
        return graphVisualizer;
    }

    private JComponent createTreeGraph(String graphDescriptor) {
        TreeBuild builder = new TreeBuild();
        NodePlace placeNode = new PlaceNode2();
        Node node = builder.create(new StringReader(graphDescriptor));
        return new TreeVisualizer(null, node, placeNode);
    }

    private void displayGraph(final JComponent contentPanel) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GraphVisualizeTopComponent graphVisualizeTopComponent = new GraphVisualizeTopComponent(contentPanel);
                graphVisualizeTopComponent.setDisplayName(MessageFormat.format("Graph Visualization [{0}]", inferenceModel.getName()));
                graphVisualizeTopComponent.open();
                graphVisualizeTopComponent.requestActive();
            }
        });
    }

    private static class GraphVisualizeTopComponent extends TopComponent {

        private static final long serialVersionUID = 1L;

        public GraphVisualizeTopComponent(JComponent contentPanel) {
            super();
            setLayout(new BorderLayout());
            add(contentPanel, BorderLayout.CENTER);
            associateLookup(Lookups.singleton(contentPanel));
        }

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }
    }
}
