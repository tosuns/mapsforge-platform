/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.api.process;

import de.fub.mapforgeproject.api.process.Process;
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

    public ProcessNode(Process<?, ?> process) {
        super(Children.LEAF, Lookups.singleton(process));
        setDisplayName(process.getName());
        setShortDescription(process.getDescription());
        process.addProcessListener(ProcessNode.this);
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
}
