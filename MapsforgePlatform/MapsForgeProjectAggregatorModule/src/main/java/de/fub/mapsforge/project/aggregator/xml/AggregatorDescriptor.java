/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "aggregator", namespace = "http://inf.fu-berlin.de/mapsforge/aggregation/schema")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AggregatorDescriptor implements ChangeListener {

    private String name;
    private String description;
    private List<Source> datasources = new ArrayList<Source>();
    private ProcessDescriptorList pipeline = new ProcessDescriptorList();
    private String aggregationStrategy;
    private String tileCachingStrategy;
    private Properties properties = new Properties();
    private String cacheFolderPath;
    private transient final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    @XmlTransient
    private static final String PROP_NAME_NAME = "name";
    @XmlTransient
    private static final String PROP_NAME_DESCRIPTION = "description";
    @XmlTransient
    private static final String PROP_NAME_AGGREGATION_STRATEGY = "aggregationStrategy";
    @XmlTransient
    private static final String PROP_NAME_TILE_CACHING_STRATEGY = "cachingStrategy";
    @XmlTransient
    private static final String PROP_NAME_CACHE_FOLDER = "cacheFolder";
    @XmlTransient
    private static final String PROP_NAME_PROPERTIES = "properties";
    @XmlTransient
    private static final String PROP_NAME_DATASOURCE = "datasource";
    @XmlTransient
    public static final String PROP_NAME_PIPELINE = "pipeline";

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
        Object oldValue = this.name;
        this.name = name;
        pcs.firePropertyChange(PROP_NAME_NAME, oldValue, this.name);
    }

    @XmlAttribute(name = "description", required = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        Object oldValue = this.description;
        this.description = description;
        pcs.firePropertyChange(PROP_NAME_DESCRIPTION, oldValue, description);
    }

    @XmlElement(name = "pipeline", required = true)
    public ProcessDescriptorList getPipeline() {
        return pipeline;
    }

    public void setPipeline(ProcessDescriptorList pipeline) {
        Object oldValue = this.pipeline;
        this.pipeline = pipeline;
        if (pipeline != null) {
            pipeline.addChangeListener(AggregatorDescriptor.this);
        }
        pcs.firePropertyChange(PROP_NAME_PIPELINE, oldValue, pipeline);
    }

    @XmlElementWrapper(name = "datasources", required = false)
    public List<Source> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<Source> sources) {
        Object oldValue = this.datasources;
        this.datasources = sources;
        pcs.firePropertyChange(PROP_NAME_DATASOURCE, oldValue, this.datasources);
    }

    @XmlElement(name = "properties")
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        Object oldValue = this.properties;
        this.properties = properties;
        if (properties != null) {
            properties.addChangeListener(AggregatorDescriptor.this);
        }
        pcs.firePropertyChange(PROP_NAME_PROPERTIES, oldValue, properties);
    }

    @XmlAttribute(name = "aggregationStrategyClass")
    public String getAggregationStrategy() {
        return aggregationStrategy;
    }

    public void setAggregationStrategy(String aggregationStrategy) {
        Object oldValue = this.aggregationStrategy;
        this.aggregationStrategy = aggregationStrategy;
        pcs.firePropertyChange(PROP_NAME_AGGREGATION_STRATEGY, oldValue, aggregationStrategy);
    }

    @XmlAttribute(name = "tileCachingStrategyClass")
    public String getTileCachingStrategy() {
        return tileCachingStrategy;
    }

    public void setTileCachingStrategy(String tileCachingStrategy) {
        Object oldValue = this.tileCachingStrategy;
        this.tileCachingStrategy = tileCachingStrategy;
        pcs.firePropertyChange(PROP_NAME_TILE_CACHING_STRATEGY, oldValue, tileCachingStrategy);
    }

    @XmlAttribute(name = "cacheFolderPath", required = false)
    public String getCacheFolderPath() {
        return cacheFolderPath;
    }

    public void setCacheFolderPath(String cacheFolderPath) {
        Object oldValue = this.cacheFolderPath;
        this.cacheFolderPath = cacheFolderPath;
        pcs.firePropertyChange(PROP_NAME_CACHE_FOLDER, oldValue, cacheFolderPath);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == getPipeline()) {
            pcs.firePropertyChange(PROP_NAME_PIPELINE, null, getPipeline());
        } else if (e.getSource() == getProperties()) {
            pcs.firePropertyChange(PROP_NAME_PROPERTIES, null, getProperties());
        }
    }

    @Override
    public String toString() {
        return "AggregatorDescriptor{" + "name=" + name + ", description=" + description + ", datasources=" + datasources + ", pipeline=" + pipeline + ", properties=" + properties + '}';
    }
}
