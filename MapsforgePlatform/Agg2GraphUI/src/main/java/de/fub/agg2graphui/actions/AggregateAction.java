/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.actions;

import de.fub.agg2graphui.AggTopComponent;
import java.awt.event.ActionEvent;
import org.openide.nodes.Children;

/**
 *
 * @author Serdar
 */
public class AggregateAction extends NodeAction {

    private final AggTopComponent view;

    public AggregateAction(AggTopComponent view) {
        super(Children.LEAF);
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
