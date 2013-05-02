/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.impl;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.text.MessageFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import weka.classifiers.Classifier;
import weka.core.Drawable;
import weka.gui.graphvisualizer.GraphVisualizer;

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
                            GraphVisualizer graphVisualizer = new GraphVisualizer();
                            graphVisualizer.readBIF(graphDescriptor);
                            graphVisualizer.layoutGraph();
                            panel = graphVisualizer;
                        }
                        break;
                    case Drawable.Newick:
                        break;
                    case Drawable.TREE:
                        graphDescriptor = graph.graph();
                        if (graphDescriptor != null) {
                            GraphVisualizer graphVisualizer = new GraphVisualizer();
                            graphVisualizer.readDOT(new StringReader(graphDescriptor));
                            graphVisualizer.layoutGraph();
                            panel = graphVisualizer;
//
//                            TreeBuild builder = new TreeBuild();
//                            NodePlace placeNode = new PlaceNode2();
//                            Node node = builder.create(new StringReader(graphDescriptor));
//                            panel = new TreeVisualizer(null, node, placeNode);
                        }
                        break;
                    default:
                        break;
                }
                if (panel != null) {
                    panel.revalidate();
                    panel.repaint();
                    JPanel contentPanel = new JPanel(new BorderLayout());
                    contentPanel.add(panel, BorderLayout.CENTER);
                    contentPanel.setPreferredSize(new Dimension(400, 300));
                    contentPanel.revalidate();
                    JFrame frame = new JFrame(MessageFormat.format("{0} {1}", inferenceModel.getName(), Bundle.CLT_Tree_Visualization()));
                    frame.setContentPane(contentPanel);
                    frame.setPreferredSize(contentPanel.getPreferredSize());
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    frame.setLocation((screenSize.width - contentPanel.getPreferredSize().width) / 2,
                            (screenSize.height - contentPanel.getPreferredSize().height) / 2);
                    frame.pack();
                    frame.setVisible(true);
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
}
