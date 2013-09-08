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
package de.fub.maps.project.aggregator.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "aggregator", namespace = "http://inf.fu-berlin.de/mapsforge/aggregation/schema")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AggregatorDescriptor {

    private String name;
    private String description;
    private List<Source> datasources = Collections.synchronizedList(new ArrayList<Source>());
    private ProcessDescriptorList pipeline = new ProcessDescriptorList();
    private String aggregationStrategy;
    private String tileCachingStrategy;
    private Properties properties = new Properties();
    private String cacheFolderPath;

    public AggregatorDescriptor() {
    }

    public AggregatorDescriptor(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @XmlAttribute(name = "name", required = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "description", required = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "pipeline", required = true)
    public ProcessDescriptorList getPipeline() {
        return pipeline;
    }

    public void setPipeline(ProcessDescriptorList pipeline) {
        this.pipeline = pipeline;
    }

    @XmlElementWrapper(name = "datasources", required = false)
    public List<Source> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<Source> sources) {
        datasources = sources;
    }

    @XmlElement(name = "properties")
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @XmlAttribute(name = "aggregationStrategyClass")
    public String getAggregationStrategy() {
        return aggregationStrategy;
    }

    public void setAggregationStrategy(String aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
    }

    @XmlAttribute(name = "tileCachingStrategyClass")
    public String getTileCachingStrategy() {
        return tileCachingStrategy;
    }

    public void setTileCachingStrategy(String tileCachingStrategy) {
        this.tileCachingStrategy = tileCachingStrategy;
    }

    @XmlAttribute(name = "cacheFolderPath", required = false)
    public String getCacheFolderPath() {
        return cacheFolderPath;
    }

    public void setCacheFolderPath(String cacheFolderPath) {
        this.cacheFolderPath = cacheFolderPath;
    }

    @Override
    public String toString() {
        return "AggregatorDescriptor{" + "name=" + name + ", description=" + description + ", datasources=" + datasources + ", pipeline=" + pipeline + ", properties=" + properties + '}';
    }
}
