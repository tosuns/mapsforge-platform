/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
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

        public static MergeHandler find(String qualifiedName) {
            return DescriptorFactory.find(MergeHandler.class, qualifiedName);
        }

        public static MergeHandler find(String qualifiedName, Aggregator aggregator) {
            return DescriptorFactory.find(MergeHandler.class, qualifiedName, aggregator);
        }
    }
}
