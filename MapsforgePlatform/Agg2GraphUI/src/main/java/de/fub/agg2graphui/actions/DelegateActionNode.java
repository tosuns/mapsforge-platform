/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Serdar
 */
public class DelegateActionNode extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final NodeAction node;

    public DelegateActionNode(NodeAction node) {
        super();
        assert node != null;
        this.node = node;
        init();
    }

    private void init() {
        putValue(AbstractAction.NAME, node.getDisplayName());
        putValue(AbstractAction.SHORT_DESCRIPTION, node.getShortDescription());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        node.actionPerformed(e);
    }
}
