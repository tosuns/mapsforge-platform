/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.wizards.detector;

import java.text.MessageFormat;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

@NbBundle.Messages({
    "CLT_CommonDetecotrInformation_Name=Name & Description"
})
public final class CommonDetectorInformationVisualPanel extends JPanel implements DocumentListener {

    private static final long serialVersionUID = 1L;
    private DataObject dataObject;

    /**
     * Creates new form CommonDetectorInformationVisualPanel
     */
    public CommonDetectorInformationVisualPanel() {
        initComponents();
        detectorName.getDocument().addDocumentListener(
                WeakListeners.document(
                CommonDetectorInformationVisualPanel.this,
                detectorName.getDocument()));
        detectorName.setText(NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.detectorName.text"));
        detectorName.setSelectionStart(0);
        int lastIndexOf = detectorName.getText().lastIndexOf(".");
        detectorName.setSelectionEnd(lastIndexOf > -1 ? lastIndexOf - 1 : 0);
        detectorName.requestFocus();
    }

    @Override
    public String getName() {
        return Bundle.CLT_CommonDetecotrInformation_Name();
    }

    public JTextArea getDetectorDescription() {
        return detectorDescription;
    }

    public JTextField getDetectorName() {
        return detectorName;
    }

    public JRadioButton getViaDetectorTemplate() {
        return viaDetectorTemplate;
    }

    public JRadioButton getViaNewDetector() {
        return viaNewDetector;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        detectorName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        detectorDescription = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        viaDetectorTemplate = new javax.swing.JRadioButton();
        viaNewDetector = new javax.swing.JRadioButton();
        filelocation = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        detectorName.setText(org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.detectorName.text")); // NOI18N

        detectorDescription.setColumns(20);
        detectorDescription.setRows(5);
        jScrollPane1.setViewportView(detectorDescription);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.jLabel2.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.jPanel1.border.title"))); // NOI18N

        buttonGroup1.add(viaDetectorTemplate);
        org.openide.awt.Mnemonics.setLocalizedText(viaDetectorTemplate, org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.viaDetectorTemplate.text")); // NOI18N

        buttonGroup1.add(viaNewDetector);
        viaNewDetector.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(viaNewDetector, org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.viaNewDetector.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(viaDetectorTemplate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viaNewDetector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viaDetectorTemplate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viaNewDetector)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        filelocation.setEditable(false);
        filelocation.setText(org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.filelocation.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CommonDetectorInformationVisualPanel.class, "CommonDetectorInformationVisualPanel.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filelocation, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(detectorName))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filelocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(detectorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(8, 8, 8)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextArea detectorDescription;
    private javax.swing.JTextField detectorName;
    private javax.swing.JTextField filelocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton viaDetectorTemplate;
    private javax.swing.JRadioButton viaNewDetector;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateLocation();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateLocation();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateLocation();
    }

    private void updateLocation() {
        if (dataObject != null) {
            String detector = getDetectorName().getText();
            if (detector == null) {
                detector = "";
            }
            int lastIndexOf = detector.lastIndexOf(".");
            if (lastIndexOf == -1) {
                filelocation.setText(MessageFormat.format("{0}/{1}.dec", dataObject.getPrimaryFile().getPath(), detector));
            } else {
                filelocation.setText(MessageFormat.format("{0}/{1}", dataObject.getPrimaryFile().getPath(), detector));
            }
        }
    }

    public JTextField getFilelocation() {
        return filelocation;
    }

    void setFolder(DataObject dataObject) {
        this.dataObject = dataObject;
        updateLocation();
    }
}
