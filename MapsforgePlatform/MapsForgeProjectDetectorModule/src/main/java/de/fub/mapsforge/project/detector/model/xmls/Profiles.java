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
@XmlType(name = "profiles")
@XmlAccessorType(XmlAccessType.FIELD)
public class Profiles {

    @XmlElement(name = "profile")
    private List<Profile> profileList = new ArrayList<Profile>();
    @XmlAttribute(name = "activeProfile")
    private String activeProfileName;

    public Profiles() {
    }

    public List<Profile> getProfileList() {
        return profileList;
    }

    public String getActiveProfile() {
        if (activeProfileName == null) {
            if (!profileList.isEmpty()) {
                activeProfileName = profileList.iterator().next().getName();
            }
        }
        return activeProfileName;
    }

    public void setActiveProfile(String activeProfileName) {
        this.activeProfileName = activeProfileName;
    }
}
