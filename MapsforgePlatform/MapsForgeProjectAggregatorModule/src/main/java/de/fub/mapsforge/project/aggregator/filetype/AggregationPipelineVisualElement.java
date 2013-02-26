/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.filetype;

import de.fub.mapsforge.project.aggregator.factories.nodes.AggregatorNode;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import de.fub.mapsforge.project.utils.AggregateUtils;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_AggregationBuilder_PIPELINE",
        iconBase = "de/fub/mapsforge/project/aggregator/filetype/aggregationBuilderIcon.png",
        mimeType = "text/aggregationbuilder+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "AggregationPipelineVisual",
        position = 2000)
@Messages("LBL_AggregationBuilder_PIPELINE=Pipeline")
public final class AggregationPipelineVisualElement extends JPanel implements MultiViewElement, PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(AggregationPipelineVisualElement.class.getName());
    private static final long serialVersionUID = 1L;
    private transient final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private transient final GraphUpdater graphUpdater = new GraphUpdater();
    private transient final ModelUpdater modelUpdater = new ModelUpdater();
    private final InstanceContent content = new InstanceContent();
    private AbstractLookup lookup = null;
    private transient Aggregator aggregator;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;

    public AggregationPipelineVisualElement(Lookup lkp) {
        AggregatorNode node = lkp.lookup(AggregatorNode.class);
        if (node != null) {
            aggregator = node.getLookup().lookup(Aggregator.class);
        }
        assert aggregator != null;
        initLookup();
        initComponents();
        aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregationPipelineVisualElement.this, aggregator));
        modelSynchronizerClient = aggregator.create(graphUpdater);
        updateGraph();
        for (Action action : getActions()) {
            toolbar.add(new JButton(action));
        }
    }

    private void initLookup() {
        lookup = new AbstractLookup(content);
        content.add(AggregateUtils.getProcessPalette());
    }

    private void updateGraph() {
        if (graphPanel1 != null) {
            graphPanel1.removeChangeListener(modelUpdater);
            graphPanel1.setAggregator(aggregator);
            graphPanel1.addChangeListener(modelUpdater);
        }
    }

    @Override
    public String getName() {
        return "AggregationBuilderVisualElement";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        graphPanel1 = new de.fub.mapsforge.project.aggregator.graph.GraphPanel();

        setLayout(new java.awt.BorderLayout());
        add(graphPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.mapsforge.project.aggregator.graph.GraphPanel graphPanel1;
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
        return new Action[]{new AbstractAction("Layout") {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    graphPanel1.layoutGraph();
                }
            }};
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
    public void propertyChange(PropertyChangeEvent evt) {
        if (Aggregator.PROP_NAME_AGGREGATOR_STATE.equals(evt.getPropertyName()) && callback != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TopComponent topComponent = callback.getTopComponent();
                    if (callback.isSelectedElement()) {
                        if (aggregator.getAggregatorState() == Aggregator.AggregatorState.RUNNING) {
                            topComponent.setIcon(aggregator.getAggregatorState().getImage());
                        } else {
                            topComponent.setIcon(aggregator.getDataObject().getNodeDelegate().getIcon(0));
                        }
                    }
                }
            });
        }
    }

    private class ModelUpdater implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (aggregator != null) {
                List<AbstractAggregationProcess<?, ?>> pipelineList = graphPanel1.collectPipeline();
                List<ProcessDescriptor> list = aggregator.getDescriptor().getPipeline().getList();
                list.clear();
                for (AbstractAggregationProcess<?, ?> process : pipelineList) {
                    if (process.getDescriptor() != null) {
                        list.add(process.getDescriptor());
                    } else {
                        LOG.log(Level.SEVERE, "process {0} doesn''T have a ProcessDescriptor", process.getName());
                    }
                }
                aggregator.notifyModified();
            }
        }
    }

    private class GraphUpdater implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            updateGraph();
        }
    }
}
