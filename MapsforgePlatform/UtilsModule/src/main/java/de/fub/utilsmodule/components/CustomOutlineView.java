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
package de.fub.utilsmodule.components;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.netbeans.swing.outline.DefaultOutlineCellRenderer;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Node.Property;

/**
 * A Custom OutlineView, which contains a custom cell renderer that does not
 * display icons.
 *
 * @author Serdar
 */
public class CustomOutlineView extends OutlineView {

    private static final long serialVersionUID = 1L;

    public CustomOutlineView() {
        super();
        init();
    }

    public CustomOutlineView(String nodesColumnLabel) {
        super(nodesColumnLabel);
        init();
    }

    private void init() {
        getOutline().setRootVisible(false);
        CustomOutlineCellRenderer customOutlineCellRenderer = new CustomOutlineCellRenderer();
        getOutline().setDefaultRenderer(Object.class, customOutlineCellRenderer);
        getOutline().setDefaultRenderer(Property.class, customOutlineCellRenderer);
    }

    private static class CustomOutlineCellRenderer extends DefaultOutlineCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setIcon(null);
                label.setToolTipText(null);
                label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                if (value instanceof Property) {
                    try {
                        label.setText(String.valueOf(((Property) value).getValue()));
                    } catch (IllegalAccessException ex) {
                    } catch (InvocationTargetException ex) {
                    }
                }
            }
            return component;
        }

        @Override
        public String getToolTipText() {
            return null;
        }
    }
}
