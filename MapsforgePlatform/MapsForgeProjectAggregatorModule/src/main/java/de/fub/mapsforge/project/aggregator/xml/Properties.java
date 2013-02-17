/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
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
@XmlType(name = "properties")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Properties implements ChangeListener {

    private List<PropertySection> sections;
    private final transient ChangeSupport pcs = new ChangeSupport(this);

    public Properties() {
    }

    public Properties(List<PropertySection> sections) {
        this.sections = sections;
    }

    @XmlElement(name = "section")
    public List<PropertySection> getSections() {
        if (sections == null) {
            sections = new ArrayList<PropertySection>();
        }
        return sections;
    }

    public void setSections(List<PropertySection> sections) {
        this.sections = sections;
        if (sections != null) {
            for (PropertySection section : sections) {
                section.addChangeListener(Properties.this);
            }
        }
        pcs.fireChange();
    }

    public void addChangeListener(ChangeListener listener) {
        pcs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        pcs.removeChangeListener(listener);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        pcs.fireChange();
    }
}
