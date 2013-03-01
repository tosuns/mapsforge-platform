/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "dataset", propOrder = {"transportmode", "url"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSet {

    @XmlAttribute(name = "url", required = true)
    private String url;
    @XmlAttribute(name = "transportmode", required = false)
    private String transportmode;

    public DataSet() {
    }

    public DataSet(String url) {
        this.url = url;
    }

    public DataSet(String url, String transportmode) {
        this.url = url;
        this.transportmode = transportmode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTransportmode() {
        return transportmode;
    }

    public void setTransportmode(String transportmode) {
        this.transportmode = transportmode;
    }

    @Override
    public String toString() {
        return "DataSet{" + "url=" + url + ", transportmode=" + transportmode + '}';
    }
}
