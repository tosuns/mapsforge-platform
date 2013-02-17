/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
 */
@XmlType(name = "pipeline")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessDescriptorList implements PropertyChangeListener {

    private List<ProcessDescriptor> list = new ArrayList<ProcessDescriptor>();
    private transient final ChangeSupport pcs = new ChangeSupport(this);
    private transient final Object MUTEX = new Object();

    @XmlElement(name = "process")
    public List<ProcessDescriptor> getList() {
        synchronized (MUTEX) {
            return list;
        }
    }

    public void setList(List<ProcessDescriptor> list) {
        synchronized (MUTEX) {
            this.list = list;
            if (list != null) {
                for (ProcessDescriptor processDescriptor : list) {
                    processDescriptor.addPropertyChangeListener(ProcessDescriptorList.this);
                }
            }
            pcs.fireChange();
        }
    }

    public void addChangeListener(ChangeListener listener) {
        pcs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        pcs.removeChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        pcs.fireChange();
    }

    @Override
    public String toString() {
        return "Processes{" + "List=" + list + '}';
    }
}
