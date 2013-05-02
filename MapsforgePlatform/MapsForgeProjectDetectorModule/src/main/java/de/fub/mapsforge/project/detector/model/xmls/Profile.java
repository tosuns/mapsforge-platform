/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import de.fub.mapsforge.project.detector.DetectorMode;
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

    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlElement(name = "preprocess", required = true)
    private Preprocess preprocess = new Preprocess();
    @XmlElement(name = "postprocess", required = true)
    private Postprocess postprocess = new Postprocess();

    public Profile() {
    }

    public Profile(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPreprocess(Preprocess preprocess) {
        this.preprocess = preprocess;
    }

    public Preprocess getPreprocess() {
        return preprocess;
    }

    public void setPostprocess(Postprocess postprocess) {
        this.postprocess = postprocess;
    }

    public Postprocess getPostprocess() {
        return postprocess;
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
