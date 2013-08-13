/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.xml.adapters;

import de.fub.maps.project.xml.ProjectFolder;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Serdar
 */
public class XmlProjectFolderAdapter extends XmlAdapter<FolderType, Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(FolderType v) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();

        for (ProjectFolder projectFolder : v.getFolder()) {
            map.put(projectFolder.getName(), projectFolder.getPath());
        }
        return map;
    }

    @Override
    public FolderType marshal(Map<String, String> v) throws Exception {
        FolderType projectFolders = new FolderType();

        for (Map.Entry<String, String> entry : v.entrySet()) {
            ProjectFolder projectFolder = new ProjectFolder();
            projectFolder.setName(entry.getKey());
            projectFolder.setPath(entry.getValue());
            projectFolders.getFolder().add(projectFolder);
        }
        return projectFolders;
    }
}
