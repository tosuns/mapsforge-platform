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
@XmlType(name = "nd")
@XmlAccessorType(XmlAccessType.FIELD)
public class Nd {

    @XmlAttribute(name = "ref", required = true)
    private long ref;

    public long getRef() {
        return ref;
    }

    public void setRef(long ref) {
        this.ref = ref;
    }
}
