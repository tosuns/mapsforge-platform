/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.ui;

import de.fub.maps.project.detector.factories.nodes.FilterNode;
import de.fub.maps.project.detector.model.process.DetectorProcess;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public final class SelectionComponent extends javax.swing.JPanel implements ActionListener, ChangeListener, PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(SelectionComponent.class.getName());
    private static final long serialVersionUID = 1L;
    private final ObservableList<DetectorProcess> allItems = new ObservableArrayList<DetectorProcess>();
    private final ObservableList<DetectorProcess> selectedItems = new ObservableArrayList<DetectorProcess>();
    private boolean hasParent = false;

    /**
     * Creates new form SelectionComponent
     */
    public SelectionComponent() {
        initComponents();
        init();
    }

    private void init() {
        allItems.clear();

        getAllListExplorerManager().setRootContext(new AbstractNode(Children.create(new DetectorProcessNodeFactory(allItems), false)));
        getSelectedListExplorerManager().setRootContext(new AbstractNode(Children.create(new DetectorProcessNodeFactory(selectedItems), false)));

        getAllListExplorerManager().addPropertyChangeListener(WeakListeners.propertyChange(SelectionComponent.this, getAllListExplorerManager()));
        getSelectedListExplorerManager().addPropertyChangeListener(WeakListeners.propertyChange(SelectionComponent.this, getSelectedListExplorerManager()));

        getToLeftButton().addActionListener(WeakListeners.create(ActionListener.class, SelectionComponent.this, getToLeftButton()));
        getToRightButton().addActionListener(WeakListeners.create(ActionListener.class, SelectionComponent.this, getToRightButton()));

        getMoveUpButton().addActionListener(WeakListeners.create(ActionListener.class, SelectionComponent.this, getMoveUpButton()));
        getMoveDownButton().addActionListener(WeakListeners.create(ActionListener.class, SelectionComponent.this, getMoveDownButton()));

        allItems.addChangeListener(WeakListeners.change(SelectionComponent.this, allItems));
        selectedItems.addChangeListener(WeakListeners.change(SelectionComponent.this, selectedItems));

        registerMouseClickListener();
        checkButtonState();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        hasParent = true;
    }

    @Override
    public void removeNotify() {
        hasParent = false;
        super.removeNotify();
    }

    public ObservableList<DetectorProcess> getAllItems() {
        return allItems;
    }

    public ObservableList<DetectorProcess> getSelectedItems() {
        return selectedItems;
    }

    private void registerMouseClickListener() {
        JViewport viewport = getAllItemList().getViewport();
        Component view = viewport.getView();
        if (view instanceof JList) {
            JList<?> jList = (JList<?>) view;

            jList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ExplorerManager explorerManager = getAllListExplorerManager();
                    propertyChange(new PropertyChangeEvent(explorerManager, ExplorerManager.PROP_SELECTED_NODES, null, explorerManager.getSelectedNodes()));
                }
            });
        }
        viewport = getSelectedItemList().getViewport();
        view = viewport.getView();
        if (view instanceof JList) {
            JList<?> jList = (JList<?>) view;

            jList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ExplorerManager explorerManager = getSelectedListExplorerManager();
                    propertyChange(new PropertyChangeEvent(explorerManager, ExplorerManager.PROP_SELECTED_NODES, null, explorerManager.getSelectedNodes()));
                }
            });
        }
    }

    private void checkButtonState() {
        getToRightButton().setEnabled(!allItems.isEmpty());
        getToLeftButton().setEnabled(!selectedItems.isEmpty());
    }

    private void setSelectItem(final DetectorProcess detectorProcess) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Children children = getSelectedListExplorerManager().getRootContext().getChildren();
                for (Node node : children.getNodes(true)) {
                    DetectorProcess dp = node.getLookup().lookup(DetectorProcess.class);
                    if (detectorProcess.equals(dp)) {
                        try {
                            getSelectedListExplorerManager().setSelectedNodes(new Node[]{getSelectedListExplorerManager().getRootContext()});
                            getSelectedListExplorerManager().setSelectedNodes(new Node[]{node});
                        } catch (PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        break;
                    }
                }
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<DetectorProcess> newList = new ArrayList<DetectorProcess>();

        if (getMoveUpButton().equals(e.getSource())) {
            Node[] selectedNodes = getSelectedListExplorerManager().getSelectedNodes();
            if (selectedNodes.length == 1) {
                DetectorProcess detectorProcess = selectedNodes[0].getLookup().lookup(DetectorProcess.class);
                int indexOf = selectedItems.indexOf(detectorProcess);
                if (indexOf > 0) {
                    selectedItems.remove(detectorProcess);
                    selectedItems.add(--indexOf, detectorProcess);
                    setSelectItem(detectorProcess);
                }
            }

        } else if (getMoveDownButton().equals(e.getSource())) {
            Node[] selectedNodes = getSelectedListExplorerManager().getSelectedNodes();
            if (selectedNodes.length == 1) {
                DetectorProcess detectorProcess = selectedNodes[0].getLookup().lookup(DetectorProcess.class);
                int indexOf = selectedItems.indexOf(detectorProcess);
                if (indexOf < selectedItems.size() - 1) {
                    selectedItems.remove(detectorProcess);
                    selectedItems.add(++indexOf, detectorProcess);
                    setSelectItem(detectorProcess);
                }
            }
        } else if (getToLeftButton().equals(e.getSource())) {
            Node[] selectedNodes = getSelectedListExplorerManager().getSelectedNodes();
            if (selectedNodes.length > 0) {
                for (Node node : selectedNodes) {
                    DetectorProcess detectorProcess = node.getLookup().lookup(DetectorProcess.class);
                    if (detectorProcess != null) {

                        newList.add(detectorProcess);
                    }
                }
                allItems.addAll(newList);
                selectedItems.removeAll(allItems);
            }
        } else if (getToRightButton().equals(e.getSource())) {
            Node[] selectedNodes = getAllListExplorerManager().getSelectedNodes();
            if (selectedNodes.length > 0) {
                for (Node node : selectedNodes) {
                    DetectorProcess detectorProcess = node.getLookup().lookup(DetectorProcess.class);
                    if (detectorProcess != null) {
                        newList.add(detectorProcess);
                    }
                }
                selectedItems.addAll(newList);
                allItems.removeAll(selectedItems);
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        checkButtonState();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (hasParent) {
            if (ExplorerManager.PROP_NODE_CHANGE.equals(evt.getPropertyName())) {
                if (getAllListExplorerManager().equals(evt.getSource())) {
                } else if (getSelectedListExplorerManager().equals(evt.getSource())) {
                    Object newValue = evt.getNewValue();
                    Object oldValue = evt.getOldValue();
                    LOG.info(MessageFormat.format("{0}. {1}", newValue, oldValue));
                }
            } else if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] selectedNodes = new Node[0];
                if (getAllListExplorerManager().equals(evt.getSource())) {
                    selectedNodes = getAllListExplorerManager().getSelectedNodes();
                } else if (getSelectedListExplorerManager().equals(evt.getSource())) {
                    selectedNodes = getSelectedListExplorerManager().getSelectedNodes();

                    if (selectedNodes.length == 1) {
                        DetectorProcess detectorProcess = selectedNodes[0].getLookup().lookup(DetectorProcess.class);
                        int indexOf = selectedItems.indexOf(detectorProcess);
                        getMoveUpButton().setEnabled(indexOf != 0);
                        getMoveDownButton().setEnabled(indexOf != selectedItems.size() - 1);
                    } else if (selectedNodes.length == 0) {
                        getMoveUpButton().setEnabled(false);
                        getMoveDownButton().setEnabled(false);
                    }
                }
                if (selectedNodes.length == 1) {
                    DetectorProcess detectorProcess = selectedNodes[0].getLookup().lookup(DetectorProcess.class);
                    if (detectorProcess != null) {
                        getDescription().setText(detectorProcess.getDescription());
                    }
                } else if (selectedNodes.length > 1) {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (Node node : selectedNodes) {
                        DetectorProcess detectorProcess = node.getLookup().lookup(DetectorProcess.class);
                        if (detectorProcess != null) {
                            stringBuilder.append(detectorProcess.toString()).append("/\n");
                        }
                    }
                    stringBuilder.delete(stringBuilder.toString().lastIndexOf("/"), stringBuilder.toString().length() - 1);
                    getDescription().setText(stringBuilder.toString());
                } else {
                    getDescription().setText(null);
                }
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

        jPanel1 = new javax.swing.JPanel();
        allItemList = new de.fub.maps.project.detector.ui.ListComponent();
        jPanel2 = new javax.swing.JPanel();
        SelectedItemList = new de.fub.maps.project.detector.ui.ListComponent();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        moveUpButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        toRightButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        toLeftButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        moveDownButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setMinimumSize(new java.awt.Dimension(400, 110));
        setPreferredSize(new java.awt.Dimension(400, 300));

        jPanel1.setMinimumSize(new java.awt.Dimension(150, 41));
        jPanel1.setPreferredSize(new java.awt.Dimension(150, 362));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(allItemList, java.awt.BorderLayout.CENTER);

        jPanel2.setMinimumSize(new java.awt.Dimension(150, 41));
        jPanel2.setPreferredSize(new java.awt.Dimension(150, 362));
        jPanel2.setLayout(new java.awt.BorderLayout());

        SelectedItemList.setPreferredSize(new java.awt.Dimension(150, 148));
        jPanel2.add(SelectedItemList, java.awt.BorderLayout.CENTER);

        jPanel3.setMaximumSize(new java.awt.Dimension(65, 2147483647));
        jPanel3.setMinimumSize(new java.awt.Dimension(65, 46));
        jPanel3.setPreferredSize(new java.awt.Dimension(65, 54));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel4.setMinimumSize(new java.awt.Dimension(100, 46));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel4.add(filler1);

        org.openide.awt.Mnemonics.setLocalizedText(moveUpButton, org.openide.util.NbBundle.getMessage(SelectionComponent.class, "SelectionComponent.moveUpButton.text")); // NOI18N
        moveUpButton.setEnabled(false);
        moveUpButton.setMaximumSize(new java.awt.Dimension(49, 23));
        moveUpButton.setPreferredSize(new java.awt.Dimension(49, 23));
        jPanel4.add(moveUpButton);
        jPanel4.add(filler5);

        org.openide.awt.Mnemonics.setLocalizedText(toRightButton, org.openide.util.NbBundle.getMessage(SelectionComponent.class, "SelectionComponent.toRightButton.text")); // NOI18N
        jPanel4.add(toRightButton);
        jPanel4.add(filler3);

        org.openide.awt.Mnemonics.setLocalizedText(toLeftButton, org.openide.util.NbBundle.getMessage(SelectionComponent.class, "SelectionComponent.toLeftButton.text")); // NOI18N
        jPanel4.add(toLeftButton);
        jPanel4.add(filler4);

        org.openide.awt.Mnemonics.setLocalizedText(moveDownButton, org.openide.util.NbBundle.getMessage(SelectionComponent.class, "SelectionComponent.moveDownButton.text")); // NOI18N
        moveDownButton.setEnabled(false);
        moveDownButton.setMaximumSize(new java.awt.Dimension(49, 23));
        moveDownButton.setMinimumSize(new java.awt.Dimension(49, 23));
        moveDownButton.setPreferredSize(new java.awt.Dimension(49, 23));
        jPanel4.add(moveDownButton);
        jPanel4.add(filler2);

        jPanel3.add(jPanel4, new java.awt.GridBagConstraints());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SelectionComponent.class, "SelectionComponent.jLabel1.text")); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        description.setEditable(false);
        description.setColumns(20);
        description.setLineWrap(true);
        description.setRows(3);
        description.setWrapStyleWord(true);
        description.setOpaque(false);
        jScrollPane1.setViewportView(description);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.maps.project.detector.ui.ListComponent SelectedItemList;
    private de.fub.maps.project.detector.ui.ListComponent allItemList;
    private javax.swing.JTextArea description;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JButton toLeftButton;
    private javax.swing.JButton toRightButton;
    // End of variables declaration//GEN-END:variables

    public JLabel getAllItemListTitle() {
        return allItemList.getTitle();
    }

    public JLabel getSelectedItemListTitle() {
        return SelectedItemList.getTitle();
    }

    public ExplorerManager getAllListExplorerManager() {
        return allItemList.getExplorerManager();
    }

    public ExplorerManager getSelectedListExplorerManager() {
        return SelectedItemList.getExplorerManager();
    }

    public ListView getAllItemList() {
        return allItemList.getListView();
    }

    public ListView getSelectedItemList() {
        return SelectedItemList.getListView();
    }

    public JTextArea getDescription() {
        return description;
    }

    public JButton getToLeftButton() {
        return toLeftButton;
    }

    public JButton getToRightButton() {
        return toRightButton;
    }

    public JButton getMoveDownButton() {
        return moveDownButton;
    }

    public JButton getMoveUpButton() {
        return moveUpButton;
    }

    private static class DetectorProcessNodeFactory extends ChildFactory<DetectorProcess> implements ChangeListener {

        private final ObservableList<DetectorProcess> list;

        public DetectorProcessNodeFactory(ObservableList<DetectorProcess> list) {
            this.list = list;
            list.addChangeListener(WeakListeners.change(DetectorProcessNodeFactory.this, list));
        }

        @Override
        protected boolean createKeys(List<DetectorProcess> toPopulate) {
            ArrayList<DetectorProcess> arrayList = new ArrayList<DetectorProcess>(list);
            toPopulate.addAll(arrayList);
            return true;
        }

        @Override
        protected Node createNodeForKey(DetectorProcess featureProcess) {
            return new FilterNode(featureProcess) {
                @Override
                public String getShortDescription() {
                    return null;
                }
            };
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            refresh(true);
        }
    }
}
