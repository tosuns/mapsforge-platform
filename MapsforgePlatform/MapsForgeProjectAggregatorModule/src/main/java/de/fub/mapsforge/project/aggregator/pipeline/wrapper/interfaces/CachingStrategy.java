/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.DefaultCachingStrategy;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.models.Aggregator;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface CachingStrategy extends ICachingStrategy, Descriptor {

    public PropertySection getPropertySection();

    public static class Factory {

        public static Collection<? extends CachingStrategy> findAll() {
            return DescriptorFactory.findAll(CachingStrategy.class);
        }

        public static CachingStrategy find(String qualifiedName) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(CachingStrategy.class, qualifiedName);
        }

        public static CachingStrategy find(String qualifiedName, Aggregator aggregator) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(CachingStrategy.class, qualifiedName, aggregator);
        }

        public static CachingStrategy getDefault() throws DescriptorFactory.InstanceNotFountException {
            return find(DefaultCachingStrategy.class.getName());
        }
    }
}
