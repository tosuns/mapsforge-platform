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
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "way")
@XmlAccessorType(XmlAccessType.FIELD)
public class Way {

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "changeset")
    protected String changeset;
    @XmlAttribute(name = "visible", required = true)
    protected boolean visible;
    @XmlAttribute(name = "user")
    protected String user;
    @XmlAttribute(name = "timestamp")
    protected String timestamp;
    @XmlElement(name = "nd", type = Nd.class)
    private List<Nd> nds = new ArrayList<Nd>();
    @XmlElement(name = "tag", type = Tag.class)
    private List<Tag> tags = new ArrayList<Tag>();

    public List<Nd> getNds() {
        return nds;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChangeset() {
        return changeset;
    }

    public void setChangeset(String changeset) {
        this.changeset = changeset;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
