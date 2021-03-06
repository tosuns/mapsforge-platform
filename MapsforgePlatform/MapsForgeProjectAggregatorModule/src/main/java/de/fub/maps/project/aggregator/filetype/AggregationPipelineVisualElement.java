/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.aggregator.filetype;

import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.models.Aggregator;
import de.fub.maps.project.utils.AggregatorUtils;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_AggregationBuilder_PIPELINE",
        iconBase = "de/fub/maps/project/aggregator/filetype/aggregationBuilderIcon.png",
        mimeType = "text/aggregationbuilder+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "AggregationPipelineVisual",
        position = 2000)
@Messages("LBL_AggregationBuilder_PIPELINE=Pipeline")
public final class AggregationPipelineVisualElement extends JPanel implements MultiViewElement, PropertyChangeListener {

    @StaticResource
    private static final String LAYOUT_BUTTON_ICON_PATH = "de/fub/maps/project/aggregator/graph/layoutIcon.png";
    private static final Logger LOG = Logger.getLogger(AggregationPipelineVisualElement.class.getName());
    private static final long serialVersionUID = 1L;
    private transient final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private transient final GraphUpdater graphUpdater = new GraphUpdater();
    private transient final ModelUpdater modelUpdater = new ModelUpdater();
    private final InstanceContent content = new InstanceContent();
    private Lookup lookup = null;
    private transient Aggregator aggregator;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;

    public AggregationPipelineVisualElement(Lookup lkp) {
        DataObject dataObject = lkp.lookup(DataObject.class);
        if (dataObject != null) {
            aggregator = dataObject.getNodeDelegate().getLookup().lookup(Aggregator.class);
        }
        assert aggregator != null;
        initComponents();
        initLookup();
        aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregationPipelineVisualElement.this, aggregator));
        modelSynchronizerClient = aggregator.create(graphUpdater);
        updateGraph();
        toolbar.add(new JToolBar.Separator());
        toolbar.add(new JButton(
                new LayoutAction(
                        null,
                        ImageUtilities.loadImageIcon(LAYOUT_BUTTON_ICON_PATH, true))));

    }

    private void initLookup() {
        lookup = new ProxyLookup(new AbstractLookup(content), graphPanel1.getLookup(), Lookups.fixed(AggregationPipelineVisualElement.this));
        content.add(AggregatorUtils.getProcessPalette());
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

        graphPanel1 = new de.fub.maps.project.aggregator.graph.GraphPanel();

        setLayout(new java.awt.BorderLayout());
        add(graphPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.maps.project.aggregator.graph.GraphPanel graphPanel1;
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
        Action[] retValue;
        // the multiviewObserver was passed to the element in setMultiViewCallback() method.
        if (callback != null) {
            retValue = callback.createDefaultActions();
            // add you own custom actions here..
        } else {
            // fallback..
            retValue = new Action[0];
        }
        return retValue;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
        graphPanel1.layoutGraph();
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
        if (this.callback != null && aggregator != null) {
            this.callback.getTopComponent().setDisplayName(aggregator.getAggregatorDescriptor().getName());
        }
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
                        topComponent.setIcon(aggregator.getDataObject().getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16));
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
                List<ProcessDescriptor> list = aggregator.getAggregatorDescriptor().getPipeline().getList();
                list.clear();
                for (AbstractAggregationProcess<?, ?> process : pipelineList) {
                    if (process.getProcessDescriptor() != null) {
                        list.add(process.getProcessDescriptor());
                    } else {
                        LOG.log(Level.SEVERE, "process {0} doesn''T have a ProcessDescriptor", process.getName());
                    }
                }
                aggregator.updateSource();
            }
        }
    }

    private class GraphUpdater implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            updateGraph();
        }
    }

    @Messages({"CLT_LayoutAction_Name=Layout Graph"})
    private class LayoutAction extends AbstractAction {

        public LayoutAction(String name, Icon icon) {
            super(name, icon);
            putValue(Action.SHORT_DESCRIPTION, Bundle.CLT_LayoutAction_Name());
        }
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            graphPanel1.layoutGraph();
        }
    }
}
