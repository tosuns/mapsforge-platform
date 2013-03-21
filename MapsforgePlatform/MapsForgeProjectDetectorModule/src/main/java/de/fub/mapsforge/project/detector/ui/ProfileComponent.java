/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.ui;

import de.fub.mapsforge.project.detector.DetectorMode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.Profile;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class ProfileComponent extends javax.swing.JPanel implements ExplorerManager.Provider, Lookup.Provider, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private Profile profile;
    private Lookup lookup;
    private Detector detector;
    private static final Profile EMPTY_PROFILE = createEmptyProfile();
    private final ObservableArrayList<Profile> profileList = new ObservableArrayList<Profile>();

    /**
     * Creates new form ProfileComponent
     */
    public ProfileComponent() {
        initComponents();
        listView1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public ProfileComponent(Lookup lkp) {
        this();
        assert lkp != null;
        detector = lkp.lookup(Detector.class);
        assert detector != null : "Lookup does not contain a detector instance!";
        lookup = ExplorerUtils.createLookup(explorerManager, getActionMap());
        profileList.addAll(detector.getDetectorDescriptor().getProfiles().getProfileList());

        explorerManager.setRootContext(new AbstractNode(Children.create(new ProfileNodeFactory(profileList), true)));
        explorerManager.addPropertyChangeListener(WeakListeners.propertyChange(ProfileComponent.this, explorerManager));
        this.profile = createEmptyProfile();
        init();
    }

    private static Profile createEmptyProfile() {
        Profile emptyProfile = new Profile();
        emptyProfile.getPreprocess().setActive(false);
        emptyProfile.getPreprocess().setMode(DetectorMode.INFERENCE);
        emptyProfile.getInference().setChangePointSequencerActive(false);
        emptyProfile.getInference().setMode(DetectorMode.INFERENCE);
        emptyProfile.getPostprocess().setActive(false);
        emptyProfile.getPostprocess().setMode(DetectorMode.INFERENCE);
        return emptyProfile;
    }

    private void init() {
        this.profileName.setText(this.profile.getName());

        this.preprocessActive.setSelected(this.profile.getPreprocess().isActive());
        if (this.profile.getPreprocess().getMode() != null) {
            this.preprocessMode.setSelectedItem(this.profile.getPreprocess().getMode());
        } else {
            this.preprocessMode.setSelectedIndex(0);
        }

        this.inferenceActive.setSelected(this.profile.getInference().isChangePointSequencerActive());
        if (this.profile.getInference().getMode() != null) {
            this.inferenceMode.setSelectedItem(this.profile.getInference().getMode());
        } else {
            this.inferenceMode.setSelectedIndex(0);
        }

        this.postprocessActive.setSelected(this.profile.getPostprocess().isActive());
        if (this.profile.getPostprocess().getMode() != null) {
            this.postprocessMode.setSelectedItem(this.profile.getPostprocess().getMode());
        } else {
            this.postprocessMode.setSelectedIndex(0);
        }
    }

    @SuppressWarnings("unchecked")
    private void readValues() {
        this.profile.setName(this.profileName.getText());

        this.profile.getPreprocess().setActive(this.preprocessActive.isSelected());
        this.profile.getPreprocess().setMode((DetectorMode) this.preprocessMode.getSelectedItem());

        this.profile.getInference().setChangePointSequencerActive(this.inferenceActive.isSelected());
        this.profile.getInference().setMode((DetectorMode) this.inferenceMode.getSelectedItem());

        this.profile.getPostprocess().setActive(this.postprocessActive.isSelected());
        this.profile.getPostprocess().setMode((DetectorMode) this.postprocessMode.getSelectedItem());

    }

    public Profile getProfile() {
        readValues();
        return profile;
    }

    private ComboBoxModel<DetectorMode> getDefaultModel() {
        ComboBoxModel<DetectorMode> model = new DefaultComboBoxModel<DetectorMode>(DetectorMode.values());
        return model;
    }

    private ComboBoxModel<DetectorMode> getReadOnlyModel() {
        return new DefaultComboBoxModel<DetectorMode>(new DetectorMode[]{DetectorMode.INFERENCE});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        inferenceActive = new javax.swing.JCheckBox();
        inferenceMode = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        postprocessActive = new javax.swing.JCheckBox();
        postprocessMode = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        profileName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        preprocessActive = new javax.swing.JCheckBox();
        preprocessMode = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        deleteButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        listView1 = new CustomListView();
        jLabel5 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(460, 370));
        setPreferredSize(new java.awt.Dimension(460, 370));
        setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 1, 1, 8));
        jPanel4.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jLabel1.text")); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(inferenceActive, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.inferenceActive.text")); // NOI18N
        inferenceActive.setToolTipText(org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.inferenceActive.toolTipText")); // NOI18N
        inferenceActive.setIconTextGap(30);
        inferenceActive.setMargin(new java.awt.Insets(2, -2, 2, 2));

        inferenceMode.setModel(getDefaultModel());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(inferenceActive)
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inferenceMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inferenceActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inferenceMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jPanel3.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(postprocessActive, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.postprocessActive.text")); // NOI18N
        postprocessActive.setIconTextGap(30);
        postprocessActive.setMargin(new java.awt.Insets(2, -2, 2, 2));

        postprocessMode.setModel(getReadOnlyModel());
        postprocessMode.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(postprocessActive)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(postprocessMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(postprocessActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(postprocessMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        profileName.setText(org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.profileName.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(preprocessActive, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.preprocessActive.text")); // NOI18N
        preprocessActive.setIconTextGap(30);
        preprocessActive.setMargin(new java.awt.Insets(2, -2, 2, 2));

        preprocessMode.setModel(getDefaultModel());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jLabel2.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(preprocessActive)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preprocessMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(preprocessActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(preprocessMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(deleteButton, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.deleteButton.text")); // NOI18N
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.editButton.text")); // NOI18N
        editButton.setEnabled(false);
        editButton.setMaximumSize(new java.awt.Dimension(63, 23));
        editButton.setMinimumSize(new java.awt.Dimension(63, 23));
        editButton.setPreferredSize(new java.awt.Dimension(63, 23));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.addButton.text")); // NOI18N
        addButton.setActionCommand(org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.addButton.actionCommand")); // NOI18N
        addButton.setMaximumSize(new java.awt.Dimension(63, 23));
        addButton.setMinimumSize(new java.awt.Dimension(63, 23));
        addButton.setPreferredSize(new java.awt.Dimension(63, 23));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(profileName)))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(profileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteButton)
                    .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

        add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel5.setPreferredSize(new java.awt.Dimension(200, 344));

        listView1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ProfileComponent.class, "ProfileComponent.jLabel5.text")); // NOI18N
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(listView1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listView1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
        );

        add(jPanel5, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        this.profile = createEmptyProfile();
        readValues();
        detector.getDetectorDescriptor().getProfiles().getProfileList().add(this.profile);
        profileList.add(this.profile);
        try {
            explorerManager.setSelectedNodes(new Node[]{});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        profile = getExplorerManager().getSelectedNodes()[0].getLookup().lookup(Profile.class);
        if (this.profile != null && detector.getDetectorDescriptor().getProfiles().getProfileList().contains(this.profile)) {
            readValues();
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

        if (this.profile != null && detector.getDetectorDescriptor().getProfiles().getProfileList().contains(this.profile)) {
            this.detector.getDetectorDescriptor().getProfiles().getProfileList().remove(this.profile);
            profileList.remove(this.profile);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JCheckBox inferenceActive;
    private javax.swing.JComboBox inferenceMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private org.openide.explorer.view.ListView listView1;
    private javax.swing.JCheckBox postprocessActive;
    private javax.swing.JComboBox postprocessMode;
    private javax.swing.JCheckBox preprocessActive;
    private javax.swing.JComboBox preprocessMode;
    private javax.swing.JTextField profileName;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] selectedNodes = explorerManager.getSelectedNodes();
            if (selectedNodes.length == 1) {
                this.profile = selectedNodes[0].getLookup().lookup(Profile.class);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else if (selectedNodes.length == 0) {
                profile = createEmptyProfile();
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
            init();
        }
    }

    private static class ProfileNodeFactory extends ChildFactory<Profile> implements ChangeListener {

        private final ObservableArrayList<Profile> profileList;

        private ProfileNodeFactory(ObservableArrayList<Profile> profileList) {
            this.profileList = profileList;
            profileList.addChangeListener(WeakListeners.change(ProfileNodeFactory.this, profileList));
        }

        @Override
        protected boolean createKeys(List<Profile> toPopulate) {
            ArrayList<Profile> arrayList = new ArrayList<Profile>(profileList);
            Collections.sort(arrayList, new Comparator<Profile>() {
                @Override
                public int compare(Profile profile1, Profile profile2) {
                    return profile1.getName().compareToIgnoreCase(profile2.getName());
                }
            });
            toPopulate.addAll(arrayList);
            return true;
        }

        @Override
        protected Node createNodeForKey(Profile profile) {
            return new ProfileNode(profile);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            refresh(true);
        }
    }

    private static class ProfileNode extends AbstractNode {

        private final Profile profile;

        public ProfileNode(Profile profile) {
            super(Children.LEAF, Lookups.singleton(profile));
            this.profile = profile;
            setDisplayName(profile.getName());
        }
    }
}
