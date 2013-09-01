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
package de.fub.maps.project.plugins.tasks.eval;

import de.fub.maps.project.openstreetmap.service.MapProvider;
import de.fub.maps.project.plugins.mapmatcher.MapMatcher;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JButton;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class EvaluationOptionPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private MapMatcher mapMatcher = null;
    private MapProvider mapProvider = null;
    private final JButton okButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");

    /**
     * Creates new form EvaluationOptionPanel
     */
    public EvaluationOptionPanel() {
        initComponents();
        okButton.setEnabled(false);
        init();
    }

    private void init() {
        mapProviderContainer.getExplorerManager().setRootContext(new AbstractNode(Children.create(new MapProviderNodeFactory(), false)));
        mapProviderContainer.getExplorerManager().addPropertyChangeListener(EvaluationOptionPanel.this);
        mapProviderContainer.getExplorerManager().setExploredContext(mapProviderContainer.getExplorerManager().getRootContext());
        mapMatcherContainer.getExplorerManager().setRootContext(new AbstractNode(Children.create(new MapMatcherNodeFactory(), false)));
        mapMatcherContainer.getExplorerManager().addPropertyChangeListener(EvaluationOptionPanel.this);
        mapMatcherContainer.getExplorerManager().setExploredContext(mapMatcherContainer.getExplorerManager().getRootContext());
    }

    public MapMatcher getMapMatcher() {
        return mapMatcher;
    }

    public MapProvider getMapProvider() {
        return mapProvider;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton[] getButtons() {
        return new JButton[]{getOkButton(), getCancelButton()};
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            if (evt.getSource() == mapProviderContainer.getExplorerManager()) {
                Node[] selectedNodes = mapProviderContainer.getExplorerManager().getSelectedNodes();
                if (selectedNodes.length == 1) {
                    mapProvider = selectedNodes[0].getLookup().lookup(MapProvider.class);
                } else {
                    mapProvider = null;
                }
            } else if (evt.getSource() == mapMatcherContainer.getExplorerManager()) {
                Node[] selectedNodes = mapMatcherContainer.getExplorerManager().getSelectedNodes();
                if (selectedNodes.length == 1) {
                    mapMatcher = selectedNodes[0].getLookup().lookup(MapMatcher.class);
                } else {
                    mapMatcher = null;
                }
            }
            if (mapProvider != null && mapMatcher != null) {
                getOkButton().setEnabled(true);
            } else {
                getOkButton().setEnabled(false);
            }
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        mapMatcherContainer = new de.fub.utilsmodule.components.ExplorerManagerProviderPanel();
        comboboxMapMatcher = new org.openide.explorer.view.ChoiceView();
        mapProviderContainer = new de.fub.utilsmodule.components.ExplorerManagerProviderPanel();
        comboboxMapProvider = new org.openide.explorer.view.ChoiceView();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EvaluationOptionPanel.class, "EvaluationOptionPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EvaluationOptionPanel.class, "EvaluationOptionPanel.jLabel2.text")); // NOI18N

        mapMatcherContainer.setLayout(new java.awt.BorderLayout());
        mapMatcherContainer.add(comboboxMapMatcher, java.awt.BorderLayout.CENTER);

        mapProviderContainer.setLayout(new java.awt.BorderLayout());
        mapProviderContainer.add(comboboxMapProvider, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mapProviderContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                    .addComponent(mapMatcherContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mapProviderContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mapMatcherContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.ChoiceView comboboxMapMatcher;
    private org.openide.explorer.view.ChoiceView comboboxMapProvider;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private de.fub.utilsmodule.components.ExplorerManagerProviderPanel mapMatcherContainer;
    private de.fub.utilsmodule.components.ExplorerManagerProviderPanel mapProviderContainer;
    // End of variables declaration//GEN-END:variables

    private static class MapMatcherNodeFactory extends ChildFactory<MapMatcher> {

        @Override
        protected boolean createKeys(List<MapMatcher> toPopulate) {
            Collection<? extends MapMatcher> mapmatchers = MapMatcher.Factory.findAll();
            toPopulate.addAll(mapmatchers);
            return true;
        }

        @Override
        protected Node createNodeForKey(MapMatcher mapMatcher) {
            return new MapMatcherNode(mapMatcher);
        }

        private static class MapMatcherNode extends AbstractNode {

            public MapMatcherNode(MapMatcher mapMatcher) {
                super(Children.LEAF, Lookups.singleton(mapMatcher));
                setDisplayName(mapMatcher.getClass().getSimpleName());
            }
        }
    }

    private static class MapProviderNodeFactory extends ChildFactory<MapProvider> {

        @Override
        protected boolean createKeys(List<MapProvider> toPopulate) {
            ArrayList<MapProvider> mapProviders = new ArrayList<MapProvider>(MapProvider.Factory.findAll());
            Collections.sort(mapProviders, new Comparator<MapProvider>() {
                @Override
                public int compare(MapProvider o1, MapProvider o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            toPopulate.addAll(mapProviders);
            return true;
        }

        @Override
        protected Node createNodeForKey(MapProvider key) {
            return new MapProviderNode(key);
        }

        private static class MapProviderNode extends AbstractNode {

            private MapProviderNode(MapProvider mapProvider) {
                super(Children.LEAF, Lookups.singleton(mapProvider));
                setDisplayName(mapProvider.getClass().getSimpleName());
                setShortDescription(mapProvider.getDescription());
            }
        }
    }
}
