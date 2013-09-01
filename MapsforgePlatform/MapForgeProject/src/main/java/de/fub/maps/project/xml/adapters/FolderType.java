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
