/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.xml.adapters;

import de.fub.maps.project.xml.ProjectFolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "foldermap")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FolderType {

    private List<ProjectFolder> folder = new ArrayList<ProjectFolder>();

    public FolderType() {
    }

    public FolderType(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            folder.add(new ProjectFolder(e));
        }
    }

    public List<ProjectFolder> getFolder() {
        return folder;
    }

    public void setFolder(List<ProjectFolder> entry) {
        this.folder = entry;
    }
}
