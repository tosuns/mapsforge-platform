/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "transportmode")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransportMode {

    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlElement(name = "dataset")
    private List<DataSet> dataset = new ArrayList<DataSet>();

    public TransportMode() {
    }

    public TransportMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataSet> getDataset() {
        return dataset;
    }

    @Override
    public String toString() {
        return "TransportMode{" + "name=" + name + ", dataset=" + dataset + '}';
    }
}
