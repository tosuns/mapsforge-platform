/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.xml.osm;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "osm")
@XmlAccessorType(XmlAccessType.FIELD)
public class Osm {

    @XmlAttribute(name = "version")
    private String version;
    @XmlAttribute(name = "generator")
    private String generator;
    @XmlAttribute(name = "copyright")
    private String copyRight;
    @XmlAttribute(name = "attribution")
    private String attribution;
    @XmlAttribute(name = "license")
    private String license;
    @XmlElement
    private Bounds bounds;
    @XmlElement(name = "node")
    private List<Node> nodes = new ArrayList<Node>();
    @XmlElement(name = "way")
    private List<Way> ways = new ArrayList<Way>();
    @XmlElement(name = "relation")
    private List<Relation> relations = new ArrayList<Relation>();

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Way> getWays() {
        return ways;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }
}
