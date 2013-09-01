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
package de.fub.maps.project.xml;

import de.fub.maps.project.xml.adapters.XmlProjectFolderAdapter;
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
