/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui;

import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Color;
import java.text.MessageFormat;
import javax.swing.JLabel;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class LayerContainer extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private transient AbstractLayer<?> layer;

    /**
     * Creates new form LayerContainer
     */
    public LayerContainer() {
        initComponents();
    }

    public LayerContainer(AbstractLayer<?> layer) {
        this();
        assert layer != null;
        this.layer = layer;
        init();
    }

    protected AbstractLayer<?> getLayer() {
        return layer;
    }

    protected void setLayer(AbstractLayer<?> layer) {
        this.layer = layer;
        layerPanel.setLayer(layer);
    }

    private void init() {
        setLayer(layer);
        setLabelName(layer.getName());
        setLabelTooltip(layer.getDescription());
    }

    @Override
    public void setBackground(Color bg) {
        if (backgroundPanel != null) {
            backgroundPanel.setBackground(bg);
        } else {
            super.setBackground(bg);
        }
    }

    @Override
    public Color getBackground() {
        return backgroundPanel != null ? backgroundPanel.getBackground() : super.getBackground();
    }

    public void setLabelName(String name) {
        layerName.setText(MessageFormat.format(NbBundle.getMessage(LayerContainer.class, "LayerContainer.layerName.text"), name));
    }

    public void setLabelTooltip(String tooltip) {
        layerName.setToolTipText(tooltip);
    }

    public JLabel getLabel() {
        return layerName;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        layerPanel = new de.fub.agg2graphui.LayerPanel();
        namePanel = new javax.swing.JPanel();
        layerName = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153), 2));
        setMinimumSize(new java.awt.Dimension(0, 16));
        setPreferredSize(new java.awt.Dimension(0, 300));
        setLayout(new java.awt.BorderLayout());

        backgroundPanel.setBackground(new java.awt.Color(255, 255, 255));
        backgroundPanel.setPreferredSize(new java.awt.Dimension(0, 278));
        backgroundPanel.setLayout(new java.awt.BorderLayout());

        layerPanel.setBackground(new java.awt.Color(255, 255, 255));
        layerPanel.setPreferredSize(new java.awt.Dimension(0, 278));

        javax.swing.GroupLayout layerPanelLayout = new javax.swing.GroupLayout(layerPanel);
        layerPanel.setLayout(layerPanelLayout);
        layerPanelLayout.setHorizontalGroup(
            layerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 378, Short.MAX_VALUE)
        );
        layerPanelLayout.setVerticalGroup(
            layerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        backgroundPanel.add(layerPanel, java.awt.BorderLayout.CENTER);

        add(backgroundPanel, java.awt.BorderLayout.CENTER);

        namePanel.setMaximumSize(new java.awt.Dimension(32767, 20));
        namePanel.setMinimumSize(new java.awt.Dimension(0, 14));
        namePanel.setPreferredSize(new java.awt.Dimension(0, 20));
        namePanel.setLayout(new java.awt.BorderLayout());

        layerName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(layerName, org.openide.util.NbBundle.getMessage(LayerContainer.class, "LayerContainer.layerName.text")); // NOI18N
        layerName.setMaximumSize(new java.awt.Dimension(35555, 14));
        layerName.setMinimumSize(new java.awt.Dimension(0, 14));
        namePanel.add(layerName, java.awt.BorderLayout.CENTER);
        namePanel.add(jSeparator1, java.awt.BorderLayout.PAGE_START);

        add(namePanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel layerName;
    private de.fub.agg2graphui.LayerPanel layerPanel;
    private javax.swing.JPanel namePanel;
    // End of variables declaration//GEN-END:variables
}
