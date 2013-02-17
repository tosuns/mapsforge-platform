/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.actions;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public abstract class NodeAction extends AbstractNode implements ActionListener {

    public static final String PROP_NAME_ENABLED = "nodeaction.enabled";
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean enabled = true;

    public NodeAction(Children children) {
        super(children);
    }

    public NodeAction(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        Object oldValue = this.enabled;
        this.enabled = enabled;
        pcs.firePropertyChange(new PropertyChangeEvent(NodeAction.this, PROP_NAME_ENABLED, oldValue, enabled));
    }

    public void addNodeStateListener(NodeStateListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removeNodeStateListener(NodeStateListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Marker interface
     */
    public interface NodeStateListener extends PropertyChangeListener {
    }
}
