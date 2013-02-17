/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.xml;

import de.fub.mapforgeproject.xml.adapters.XmlProjectFolderAdapter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Serdar
 */
@XmlType(name = "folders")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectFolders {
    
    @XmlElement(name = "foldermap")
    @XmlJavaTypeAdapter(XmlProjectFolderAdapter.class)
    private HashMap<String, String> folderMap = new HashMap<String, String>();

    public String getFolderPath(String folderName) {
        return folderMap.get(folderName);
    }

    public String putFolder(String name, String path) {
        return folderMap.put(name, path);
    }

    public Set<Entry<String, String>> entrySet() {
        return folderMap.entrySet();
    }

    @Override
    public String toString() {
        return "ProjectFolders{" + "folderMap=" + folderMap + '}';
    }
}
