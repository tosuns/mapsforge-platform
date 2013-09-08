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
package de.fub.maps.project.utils;

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
