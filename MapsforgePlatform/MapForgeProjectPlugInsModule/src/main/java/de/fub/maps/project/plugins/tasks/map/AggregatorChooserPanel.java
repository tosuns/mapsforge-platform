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
package de.fub.maps.project.plugins.tasks.map;

import de.fub.maps.project.aggregator.filetype.AggregatorDataObject;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.components.CustomOutlineView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.util.List;
import javax.swing.Action;
import javax.swing.ListSelectionModel;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class AggregatorChooserPanel extends javax.swing.JPanel implements ExplorerManager.Provider, PropertyChangeListener {

    public static final String PROP_NAME_PANEL_CLOSED = "panelClosed";
    private static final long serialVersionUID = 1L;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private final AggregatorDataObjectPropertyEditor propertyEditor;
    private boolean panelActive = false;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private DataObject selectedAggregatorDataObject = null;

    /**
     * Creates new form AggregatorChooserPanel
     */
    public AggregatorChooserPanel(AggregatorDataObjectPropertyEditor propertyEditor) {
        assert propertyEditor != null;
        initComponents();
        this.propertyEditor = propertyEditor;
        init();
    }

    private void init() {
        outlineView1.getOutline().setRootVisible(false);
        outlineView1.getOutline().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        explorerManager.setRootContext(new AbstractNode(Children.create(new NodeFactory(), true)));
        Object value = propertyEditor.getValue();
        if (value instanceof AggregatorDataObject) {
            selectAggregator(((AggregatorDataObject) value));
        }
        explorerManager.addPropertyChangeListener(WeakListeners.propertyChange(AggregatorChooserPanel.this, explorerManager));
    }

    private void selectAggregator(AggregatorDataObject aggregatorDataObject) {
        selectedAggregatorDataObject = aggregatorDataObject;
        Children children = explorerManager.getRootContext().getChildren();

        for (Node node : children.getNodes(true)) {
            Aggregator aggregator = node.getLookup().lookup(Aggregator.class);
            if (aggregator != null) {
                aggregator.getDataObject().equals(aggregatorDataObject);
                try {
                    explorerManager.setSelectedNodes(new Node[]{node});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        panelActive = true;
    }

    @Override
    public void removeNotify() {
        Object oldValue = this.panelActive;
        panelActive = false;
        pcs.firePropertyChange(PROP_NAME_PANEL_CLOSED, oldValue, panelActive);
        super.removeNotify();
    }

    public DataObject getSelectedAggregatorDataObject() {
        return selectedAggregatorDataObject;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener);
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.removePropertyChangeListener(propertyName, listener);
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        outlineView1 = new CustomOutlineView("Aggregators");

        setLayout(new java.awt.BorderLayout());

        outlineView1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        add(outlineView1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.OutlineView outlineView1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName()) && panelActive) {
            Node[] selectedNodes = getExplorerManager().getSelectedNodes();
            if (selectedNodes.length == 1) {
                selectedAggregatorDataObject = selectedNodes[0].getLookup().lookup(DataObject.class);
            } else {
                selectedAggregatorDataObject = null;
            }
        }
    }

    private static class NodeFactory extends ChildFactory<AggregatorDataObject> {

        public NodeFactory() {
        }

        @Override
        protected boolean createKeys(List<AggregatorDataObject> toPopulate) {
            FileObject aggregatorTemplates = FileUtil.getConfigFile("Templates/Aggregators");
            if (aggregatorTemplates != null) {
                for (FileObject childFileObject : aggregatorTemplates.getChildren()) {
                    try {
                        DataObject childDataObject = DataObject.find(childFileObject);
                        if (childDataObject instanceof AggregatorDataObject) {
                            toPopulate.add(((AggregatorDataObject) childDataObject));
                        }
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(AggregatorDataObject dataObject) {
            return new AggregatorFilterNode(dataObject.getNodeDelegate());
        }
    }

    private static class AggregatorFilterNode extends FilterNode {

        public AggregatorFilterNode(Node original) {
            super(original, org.openide.nodes.Children.LEAF);
        }

        @Override
        public PropertySet[] getPropertySets() {
            return new PropertySet[0];
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }
    }
}
