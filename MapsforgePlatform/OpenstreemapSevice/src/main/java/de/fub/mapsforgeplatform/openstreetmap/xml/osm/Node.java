/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.xml.osm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Serdar
 */
@XmlType(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {

    @XmlAttribute
    private long id;
    @XmlAttribute
    private double lat;
    @XmlAttribute
    private double lon;
    @XmlAttribute
    private String user;
    @XmlAttribute
    private long uid;
    @XmlAttribute
    private boolean visible;
    @XmlAttribute
    private long version;
    @XmlAttribute
    private long changeset;
    @XmlAttribute(name = "timestamp")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlSchemaType(name = "dateTime")
    private Date timestamp;
    @XmlElement(name = "tag")
    private List<Tag> tags = new ArrayList<Tag>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getChangeset() {
        return changeset;
    }

    public void setChangeset(long changeset) {
        this.changeset = changeset;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
