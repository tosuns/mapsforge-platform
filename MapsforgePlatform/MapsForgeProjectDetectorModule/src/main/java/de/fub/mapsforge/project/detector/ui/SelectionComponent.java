/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.ui;

import de.fub.mapsforge.project.detector.factories.nodes.FilterNode;
import de.fub.mapsforge.project.detector.model.DetectorProcess;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public final class SelectionComponent extends javax.swing.JPanel implements ActionListener, ChangeListener, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private final ObservableList<DetectorProcess> allItems = new ObservableArrayList<DetectorProcess>();
    private final ObservableList<DetectorProcess> selectedItems = new ObservableArrayList<DetectorProcess>();

    /**
     * Creates new form SelectionComponent
     */
    public SelectionComponent() {
        initComponents();
        init();
    }

    private void init() {
        allItems.clear();

        getAllListExplorerManager().setRootContext(new AbstractNode(Children.create(new DetectorProcessNodeFactory(allItems), true)));
        getSelectedListExplorerManager().setRootContext(new AbstractNode(Children.create(new DetectorProcessNodeFactory(selectedItems), true)));

        getAllListExplorerManager().addPropertyChangeListener(WeakListeners.propertyChange(SelectionComponent.this, getAllListExplorerManager()));
        getSelectedListExplorerManager().addPropertyChangeListener(WeakListeners.propertyChange(SelectionComponent.this, getSelectedListExplorerManager()));

        getToLeftButton().addActionListener(WeakListeners.create(ActionListener.class, SelectionComponent.this, getToLeftButton()));
        getToRightButton().addActionListener(WeakListeners.create(ActionListener.class, SelectionComponent.this, getToRightButton()));

        allItems.addChangeListener(WeakListeners.change(SelectionComponent.this, allItems));
        selectedItems.addChangeListener(WeakListeners.change(SelectionComponent.this, selectedItems));

        registerMouseClickListener();
        checkButtonState();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<DetectorProcess> newList = new ArrayList<DetectorProcess>();
        if (getToLeftButton().equals(e.getSource())) {
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
        if (ExplorerManager.PROP_NODE_CHANGE.equals(evt.getPropertyName())) {
            if (getAllListExplorerManager().equals(evt.getSource())) {
            } else if (getSelectedListExplorerManager().equals(evt.getSource())) {
            }
        } else if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] selectedNodes = new Node[0];
            if (getAllListExplorerManager().equals(evt.getSource())) {
                selectedNodes = getAllListExplorerManager().getSelectedNodes();
            } else if (getSelectedListExplorerManager().equals(evt.getSource())) {
                selectedNodes = getSelectedListExplorerManager().getSelectedNodes();
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        allItemList = new de.fub.mapsforge.project.detector.ui.ListComponent();
        jPanel2 = new javax.swing.JPanel();
        SelectedItemList = new de.fub.mapsforge.project.detector.ui.ListComponent();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        toRightButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        toLeftButton = new javax.swing.JButton();
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

        org.openide.awt.Mnemonics.setLocalizedText(toRightButton, org.openide.util.NbBundle.getMessage(SelectionComponent.class, "SelectionComponent.toRightButton.text")); // NOI18N
        jPanel4.add(toRightButton);
        jPanel4.add(filler3);

        org.openide.awt.Mnemonics.setLocalizedText(toLeftButton, org.openide.util.NbBundle.getMessage(SelectionComponent.class, "SelectionComponent.toLeftButton.text")); // NOI18N
        jPanel4.add(toLeftButton);
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
    private de.fub.mapsforge.project.detector.ui.ListComponent SelectedItemList;
    private de.fub.mapsforge.project.detector.ui.ListComponent allItemList;
    private javax.swing.JTextArea description;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
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

    private static class DetectorProcessNodeFactory extends ChildFactory<DetectorProcess> implements ChangeListener {

        private final ObservableList<DetectorProcess> list;

        public DetectorProcessNodeFactory(ObservableList<DetectorProcess> list) {
            this.list = list;
            list.addChangeListener(WeakListeners.change(DetectorProcessNodeFactory.this, list));
        }

        @Override
        protected boolean createKeys(List<DetectorProcess> toPopulate) {
            toPopulate.addAll(list);
            Collections.sort(toPopulate);
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
