/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.maps.project.aggregator.pipeline.wrapper.aggregation.strategy.DefaultMergeHandler;
import de.fub.maps.project.aggregator.xml.PropertySet;
import de.fub.maps.project.models.Aggregator;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface MergeHandler extends IMergeHandler, Descriptor {

    public PropertySet getPropertySet();

    public static class Factory {

        public static Collection<? extends MergeHandler> findAll() {
            return DescriptorFactory.findAll(MergeHandler.class);
        }

        public static MergeHandler find(String qualifiedName) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(MergeHandler.class, qualifiedName);
        }

        public static MergeHandler find(String qualifiedName, Aggregator aggregator) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(MergeHandler.class, qualifiedName, aggregator);
        }

        public static MergeHandler getDefault() throws DescriptorFactory.InstanceNotFountException {
            return find(DefaultMergeHandler.class.getName());
        }
    }
}
