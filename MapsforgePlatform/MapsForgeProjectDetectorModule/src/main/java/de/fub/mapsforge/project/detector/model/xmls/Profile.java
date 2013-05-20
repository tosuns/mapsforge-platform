/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import de.fub.mapsforge.project.detector.DetectorMode;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "profile")
@XmlType(name = "profile", propOrder = {"name", "preprocess", "postprocess"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Profile {

    public static final String PROP_NAME_NAME = "name";
    public static final String PROP_NAME_PREPROCESS = "preprocess";
    public static final String PROP_NAME_POSTPROCESS = "postprocess";
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlElement(name = "preprocess", required = true)
    private Preprocess preprocess = new Preprocess();
    @XmlElement(name = "postprocess", required = true)
    private Postprocess postprocess = new Postprocess();
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Profile() {
    }

    public Profile(String name) {
        this.name = name;
    }

    public void setName(String name) {
        Object oldValue = this.name;
        this.name = name;
        pcs.firePropertyChange(PROP_NAME_NAME, oldValue, this.name);
    }

    public String getName() {
        return name;
    }

    public void setPreprocess(Preprocess preprocess) {
        Object oldValue = this.preprocess;
        this.preprocess = preprocess;
        pcs.firePropertyChange(PROP_NAME_PREPROCESS, oldValue, this.preprocess);
    }

    public Preprocess getPreprocess() {
        return preprocess;
    }

    public void setPostprocess(Postprocess postprocess) {
        Object oldValue = this.postprocess;
        this.postprocess = postprocess;
        pcs.firePropertyChange(PROP_NAME_POSTPROCESS, oldValue, this.postprocess);
    }

    public Postprocess getPostprocess() {
        return postprocess;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public String toString() {
        return MessageFormat.format("Profile{name={0}, preprocess={1}, postprocess={2}{3}", name, preprocess, postprocess, '}');
    }

    @XmlType(name = "preprocessor")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Preprocess {

        @XmlAttribute(name = "active")
        private boolean active = true;
        @XmlAttribute(name = "mode")
        private DetectorMode mode = DetectorMode.INFERENCE;

        public Preprocess() {
        }

        public Preprocess(boolean active, DetectorMode mode) {
            this.active = active;
            this.mode = mode;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public DetectorMode getMode() {
            return mode;
        }

        public void setMode(DetectorMode mode) {
            this.mode = mode;
        }

        @Override
        public String toString() {
            return MessageFormat.format("Preprocess{active={0}, mode={1}{2}", active, mode, '}');
        }
    }

    @XmlType(name = "postprocess")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Postprocess {

        @XmlAttribute(name = "active")
        private boolean active = true;
        @XmlAttribute(name = "mode", required = true)
        private DetectorMode mode = DetectorMode.INFERENCE;

        public Postprocess() {
        }

        public Postprocess(boolean active, DetectorMode mode) {
            this.active = active;
            this.mode = mode;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public DetectorMode getMode() {
            return mode;
        }

        public void setMode(DetectorMode mode) {
            this.mode = mode;
        }

        @Override
        public String toString() {
            return MessageFormat.format("Postprocess{active={0}, mode={1}{2}", active, mode, '}');
        }
    }
}
