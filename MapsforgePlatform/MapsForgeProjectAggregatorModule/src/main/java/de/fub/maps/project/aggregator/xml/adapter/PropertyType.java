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
package de.fub.maps.project.aggregator.xml.adapter;

import de.fub.maps.project.xml.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Serdar
 */
public class PropertyType {

    private List<Property> property = new ArrayList<Property>();

    public PropertyType() {
    }

    public PropertyType(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            property.add(new Property(e.getKey(), e.getValue()));
        }
    }

    public List<Property> getProperty() {
        return property;
    }

    public void setProperty(List<Property> entry) {
        this.property = entry;
    }
}
