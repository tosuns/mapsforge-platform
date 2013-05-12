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
@XmlType(name = "member")
@XmlAccessorType(XmlAccessType.FIELD)
public class Member {

    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "ref")
    private long ref;
    @XmlAttribute(name = "role")
    private String role;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getRef() {
        return ref;
    }

    public void setRef(long ref) {
        this.ref = ref;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
