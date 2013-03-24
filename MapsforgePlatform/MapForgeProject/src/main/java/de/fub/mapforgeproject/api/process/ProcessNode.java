/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.api.process;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class ProcessNode extends AbstractNode implements PropertyChangeListener, ProcessPipeline.ProcessListener {

    private final Process<?, ?> process;

    public ProcessNode(Process<?, ?> process) {
        super(Children.LEAF, Lookups.singleton(process));
        this.process = process;
        setDisplayName(process.getName());
        setShortDescription(process.getDescription());
        process.addProcessListener(ProcessNode.this);
    }

    public Process<?, ?> getProcess() {
        return process;
    }

    @Override
    public String getDisplayName() {
        if (process != null) {
            return process.getName();
        }
        return super.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        if (process != null) {
            return process.getDescription();
        }
        return super.getShortDescription();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireIconChange();
    }

    @Override
    public void changed(ProcessPipeline.ProcessEvent event) {
        fireIconChange();
    }

    @Override
    public void started() {
        fireIconChange();
    }

    @Override
    public void canceled() {
        fireIconChange();
    }

    @Override
    public void finished() {
        fireIconChange();
    }
}
