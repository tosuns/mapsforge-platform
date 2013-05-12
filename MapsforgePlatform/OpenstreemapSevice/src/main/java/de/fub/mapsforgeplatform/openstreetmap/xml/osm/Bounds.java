/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.xml.osm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "bounds")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bounds {

    @XmlAttribute(required = true)
    private double minlat;
    @XmlAttribute(required = true)
    private double minlon;
    @XmlAttribute(required = true)
    private double maxlat;
    @XmlAttribute(required = true)
    private double maxlon;

    public double getMinlat() {
        return minlat;
    }

    public void setMinlat(double minlat) {
        this.minlat = minlat;
    }

    public double getMinlon() {
        return minlon;
    }

    public void setMinlon(double minlon) {
        this.minlon = minlon;
    }

    public double getMaxlat() {
        return maxlat;
    }

    public void setMaxlat(double maxlat) {
        this.maxlat = maxlat;
    }

    public double getMaxlon() {
        return maxlon;
    }

    public void setMaxlon(double maxlon) {
        this.maxlon = maxlon;
    }
}
