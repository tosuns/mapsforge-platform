/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.graph;

import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.GraphLayoutFactory;
import org.netbeans.api.visual.graph.layout.GraphLayoutSupport;
import org.netbeans.api.visual.graph.layout.GridGraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
 */
public class GraphPanel extends javax.swing.JPanel implements ChangeListener {

    private static final Logger LOG = Logger.getLogger(GraphPanel.class.getName());
    private static final long serialVersionUID = 1L;
    private transient final ProcessGraph graph = new ProcessGraph();
    private transient Aggregator aggregator = null;
    private transient final ChangeSupport cs = new ChangeSupport(this);
    private boolean reinitProcess = false;

    /**
     * Creates new form GraphPanel
     */
    public GraphPanel() {
        initComponents();
        jScrollPane1.setViewportView(graph.createView());
        jScrollPane1.setDropTarget(new DropTarget(this, new DropHandler()));
    }

    public void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    public Aggregator getAggregator() {
        return aggregator;
    }

    public void setAggregator(Aggregator aggregator) {
        graph.removeChangeListener(GraphPanel.this);
        this.aggregator = aggregator;
        reinitGraph();
        graph.addChangeListener(GraphPanel.this);
    }

    protected Widget attachNodeWidget(AbstractAggregationProcess<?, ?> node) {
        return graph.attachNodeWidget(node);
    }

    protected Widget attachEdgeWidget(String edge) {
        return graph.attachEdgeWidget(edge);
    }

    protected void attachEdgeSourceAnchor(String edge, AbstractAggregationProcess<?, ?> oldSourceNode, AbstractAggregationProcess<?, ?> sourceNode) {
        graph.attachEdgeSourceAnchor(edge, oldSourceNode, sourceNode);
    }

    protected void attachEdgeTargetAnchor(String edge, AbstractAggregationProcess<?, ?> oldTargetNode, AbstractAggregationProcess<?, ?> targetNode) {
        graph.attachEdgeTargetAnchor(edge, oldTargetNode, targetNode);
    }

    public void layoutGraph() {
        Collection<AbstractAggregationProcess<?, ?>> processes = aggregator.getPipeline().getProcesses();
        final GraphLayout<AbstractAggregationProcess<?, ?>, String> layout = new GridGraphLayout<AbstractAggregationProcess<?, ?>, String>();
        AbstractAggregationProcess rootProcess = processes.iterator().next();
        GraphLayoutSupport.setTreeGraphLayoutRootNode(layout, rootProcess);
        SceneLayout sceneLayout = LayoutFactory.createSceneGraphLayout(graph, layout);
        layout.setAnimated(false);
        sceneLayout.invokeLayoutImmediately();
        graph.revalidate();
        repaint();
    }

    private void reinitGraph() {
        reinitProcess = true;
        try {
            graph.validate();

            // collected all processes that are currently in the gaphscene
            List<AbstractAggregationProcess<?, ?>> collectPipeline = collectPipeline();
            // remove all process
            for (AbstractAggregationProcess<?, ?> process : collectPipeline) {
                graph.removeNodeWithEdges(process);
                graph.validate();
            }

            Collection<AbstractAggregationProcess<?, ?>> processes = aggregator.getPipeline().getProcesses();

            Widget lastNodeWidget = null;
            AbstractAggregationProcess<?, ?> lastProcess = null;
            int i = 1;
            for (AbstractAggregationProcess<?, ?> process : processes) {
                if (process != null) {
                    Widget nodeWidget = graph.addNode(process);
                    graph.validate();
                    nodeWidget.setPreferredLocation(new Point(20 + (i * 200), 50));
                    if (lastNodeWidget != null && lastProcess != null) {
                        String edgeID = "edge" + ProcessGraph.edgeCount++;
                        graph.addEdge(edgeID);
                        graph.setEdgeSource(edgeID, lastProcess);
                        graph.setEdgeTarget(edgeID, process);
                        graph.validate();
                    }
                    i++;
                    lastNodeWidget = nodeWidget;
                    lastProcess = process;
                } else {
                    LOG.log(Level.FINE, "Error");
                }
            }
            graph.repaint();
            repaint();
        } finally {
            reinitProcess = false;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!reinitProcess) {
            cs.fireChange();
        }
    }

    /**
     * Returns a chain of process that start with an process that has as input
     * type Void.
     *
     * @return
     */
    public List<AbstractAggregationProcess<?, ?>> collectPipeline() {
        return graph.collectPipeline();
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

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    static class DropHandler extends DropTargetAdapter {

        public DropHandler() {
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
        }
    }
}
