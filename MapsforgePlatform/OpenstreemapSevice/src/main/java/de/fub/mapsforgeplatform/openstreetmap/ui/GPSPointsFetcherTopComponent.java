/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.ui;

import de.fub.mapsforgeplatform.openstreetmap.service.LocationBoundingBoxService;
import de.fub.mapsforgeplatform.openstreetmap.ui.controller.GPSPointsFetcherController;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.fub.mapsforgeplatform.openstreetmap.ui//GPSPointsFetcher//EN",
        autostore = false)
//@TopComponent.Description(
//    preferredID = "GPSPointsFetcherTopComponent",
////iconBase="SET/PATH/TO/ICON/HERE",
//persistenceType = TopComponent.PERSISTENCE_ALWAYS)
//@TopComponent.Registration(mode = "explorer", openAtStartup = true)
//@ActionID(category = "Window", id = "de.fub.mapsforgeplatform.openstreetmap.ui.GPSPointsFetcherTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
//@TopComponent.OpenActionRegistration(
//    displayName = "#CTL_GPSPointsFetcherAction",
//preferredID = "GPSPointsFetcherTopComponent")
@Messages({
    "CTL_GPSPointsFetcherAction=GPSPointsFetcher",
    "CTL_GPSPointsFetcherTopComponent=GPSPointsFetcher Window",
    "HINT_GPSPointsFetcherTopComponent=This is a GPSPointsFetcher window"
})
public final class GPSPointsFetcherTopComponent extends TopComponent implements ExplorerManager.Provider, LookupListener, ChangeListener {

    private final Object MUTEX = new Object();
    private final ExplorerManager explorerManager = new ExplorerManager();
    private static final Logger LOG = Logger.getLogger(GPSPointsFetcherTopComponent.class.getName());
    private final GPSPointsFetcherController controller;
    private final Lookup.Result<LocationBoundingBoxService> result;
    private LocationBoundingBoxService locationBoundingBoxService;

    public GPSPointsFetcherTopComponent() {
        initComponents();
        setName(Bundle.CTL_GPSPointsFetcherTopComponent());
        setToolTipText(Bundle.HINT_GPSPointsFetcherTopComponent());
        controller = new GPSPointsFetcherController(GPSPointsFetcherTopComponent.this);
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
        result = Utilities.actionsGlobalContext().lookupResult(LocationBoundingBoxService.class);
        result.addLookupListener(GPSPointsFetcherTopComponent.this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        beanTreeView = new org.openide.explorer.view.BeanTreeView();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        leftLongitude = new javax.swing.JTextField();
        bottomLatitude = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        rightLongitude = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        topLatitude = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        endPage = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        startPage = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        fetchButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        beanTreeView.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(160, 160, 160)));
        beanTreeView.setRootVisible(false);
        add(beanTreeView, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jLabel1.toolTipText")); // NOI18N

        leftLongitude.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        leftLongitude.setText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.leftLongitude.text")); // NOI18N
        leftLongitude.setToolTipText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.leftLongitude.toolTipText")); // NOI18N

        bottomLatitude.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        bottomLatitude.setText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.bottomLatitude.text")); // NOI18N
        bottomLatitude.setToolTipText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.bottomLatitude.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jLabel2.text")); // NOI18N

        rightLongitude.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rightLongitude.setText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.rightLongitude.text")); // NOI18N
        rightLongitude.setToolTipText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.rightLongitude.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jLabel3.text")); // NOI18N

        topLatitude.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        topLatitude.setText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.topLatitude.text")); // NOI18N
        topLatitude.setToolTipText(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.topLatitude.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(topLatitude)
                    .addComponent(rightLongitude)
                    .addComponent(bottomLatitude)
                    .addComponent(leftLongitude))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(leftLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bottomLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rightLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(topLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jPanel3.border.title"))); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jLabel5.text")); // NOI18N
        jLabel5.setMaximumSize(new java.awt.Dimension(24, 14));
        jLabel5.setPreferredSize(new java.awt.Dimension(14, 14));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.jLabel6.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startPage, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endPage, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(fetchButton, org.openide.util.NbBundle.getMessage(GPSPointsFetcherTopComponent.class, "GPSPointsFetcherTopComponent.fetchButton.text")); // NOI18N
        fetchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fetchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fetchButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fetchButton)
                .addGap(21, 21, 21))
        );

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void fetchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fetchButtonActionPerformed
        // TODO add your handling code here:
        try {
            if (isInputValid()) {
                controller.handleFetch();
            } else {
                throw new IllegalArgumentException();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            NotifyDescriptor nf = new NotifyDescriptor.Message("Input values are not valid!", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notifyLater(nf);
        }
    }//GEN-LAST:event_fetchButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView;
    private javax.swing.JTextField bottomLatitude;
    private javax.swing.JSpinner endPage;
    private javax.swing.JButton fetchButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField leftLongitude;
    private javax.swing.JTextField rightLongitude;
    private javax.swing.JSpinner startPage;
    private javax.swing.JTextField topLatitude;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        resultChanged(new LookupEvent(result));
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
//        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private boolean isInputValid() throws NumberFormatException, IllegalArgumentException {
        boolean result = true;

        double leftLong = Double.valueOf(leftLongitude.getText());
        double rightLong = Double.valueOf(rightLongitude.getText());
        double bottomLat = Double.valueOf(bottomLatitude.getText());
        double topLat = Double.valueOf(topLatitude.getText());

        // check whether the respective lat/long values are in the value constrains
        result = result && isValidLongitude(leftLong)
                && isValidLatitude(bottomLat)
                && isValidLongitude(rightLong)
                && isValidLatitude(topLat);

        // check whether the bounding box is koncave
        result = result && leftLong <= rightLong && bottomLat <= topLat;

        result = result && Math.max(0, Math.abs(leftLong - rightLong)) < 0.25 && Math.max(0, Math.abs(bottomLat - topLat)) < 0.25;

        // check whether the page intevale is valid
        result = result && isIntervalValid((Integer) startPage.getValue(), (Integer) startPage.getValue());

        return result;
    }

    private boolean isValidLongitude(double longitude) {
        return longitude <= 180 && longitude >= -180;
    }

    private boolean isValidLatitude(double latitude) {
        return latitude <= 90 && latitude >= -90;
    }

    private boolean isIntervalValid(int start, int end) {
        return start >= 0 && end >= start ? true : start >= 0 && end == -1;
    }

    public BeanTreeView getBeanTreeView() {
        return beanTreeView;
    }

    public JTextField getBottomLatitude() {
        return bottomLatitude;
    }

    public JSpinner getEndPage() {
        return endPage;
    }

    public JButton getFetchButton() {
        return fetchButton;
    }

    public JTextField getLeftLongitude() {
        return leftLongitude;
    }

    public JTextField getRightLongitude() {
        return rightLongitude;
    }

    public JSpinner getStartPage() {
        return startPage;
    }

    public JTextField getTopLatitude() {
        return topLatitude;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public void lockInputFields(boolean lock) {
        synchronized (MUTEX) {
            getLeftLongitude().setEnabled(!lock);
            getTopLatitude().setEnabled(!lock);
            getBottomLatitude().setEnabled(!lock);
            getRightLongitude().setEnabled(!lock);
            getFetchButton().setEnabled(!lock);
            getStartPage().setEnabled(!lock);
            getEndPage().setEnabled(!lock);
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends LocationBoundingBoxService> allInstances = result.allInstances();
        if (!allInstances.isEmpty()) {
            LocationBoundingBoxService next = allInstances.iterator().next();
            if (locationBoundingBoxService != next) {
                if (locationBoundingBoxService != null) {
                    locationBoundingBoxService.removeChangeListener(this);
                }
            }
            locationBoundingBoxService = next;
            locationBoundingBoxService.addChangeListener(GPSPointsFetcherTopComponent.this);
            updateLocation(locationBoundingBoxService.getViewBoundingBox());

        } else if (locationBoundingBoxService != null) {
            locationBoundingBoxService.removeChangeListener(this);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (locationBoundingBoxService != null) {
            updateLocation(locationBoundingBoxService.getViewBoundingBox());
        }
    }

    private void updateLocation(LocationBoundingBoxService.BoundingBox viewBoundingBox) {
        getLeftLongitude().setText(String.valueOf(viewBoundingBox.getLeftLongitude()));
        getTopLatitude().setText(String.valueOf(viewBoundingBox.getTopLatitude()));
        getRightLongitude().setText(String.valueOf(viewBoundingBox.getRightLongitude()));
        getBottomLatitude().setText(String.valueOf(viewBoundingBox.getBottomLatitude()));
    }
}
