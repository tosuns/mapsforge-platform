/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.utils;

import de.fub.agg2graphui.LayerContainer;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class LayerTableCellRender extends LayerContainer implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    public LayerTableCellRender() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Node node = Visualizer.findNode(value);
        LayerContainer comp = this;
        if (node != null) {
            AbstractLayer layer = node.getLookup().lookup(AbstractLayer.class);
            if (layer != null) {
                comp = layer.getLayerPanel();
            }
        }
        if (comp != null) {
            if (isSelected) {
                comp.setBorder(BorderFactory.createLineBorder(table.getSelectionBackground()));
                comp.getLabel().setBackground(table.getSelectionBackground());
                comp.getLabel().setForeground(table.getSelectionForeground());
                comp.getLabel().setOpaque(true);
            } else {
                comp.setBorder(BorderFactory.createLineBorder(new Color(153, 153, 153)));
                comp.getLabel().setBackground(table.getBackground());
                comp.getLabel().setForeground(table.getForeground());
                comp.getLabel().setOpaque(false);
            }
        }
        return comp;
    }
}
