/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.components;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import org.openide.explorer.view.ListView;
import org.openide.explorer.view.NodeRenderer;

/**
 * Custom List View with it's own NodeRenderer, which does not display an icon.
 *
 * @author Serdar
 */
public class CustomListView extends ListView {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public CustomListView() {
        super();
        list.setCellRenderer(new CustomNodeRenderer());
    }

    private static class CustomNodeRenderer extends NodeRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, sel, cellHasFocus);
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setToolTipText(null);
                label.setIcon(null);
                label.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
            }
            return component;
        }
    }
}
