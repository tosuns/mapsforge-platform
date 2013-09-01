/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
