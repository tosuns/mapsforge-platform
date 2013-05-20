/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.aggregation.strategy.DefaultTraceDistance;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface TraceDistance extends ITraceDistance, Descriptor {

    public PropertySet getPropertySet();

    public static class Factory {

        public static Collection<? extends TraceDistance> findAll() {
            return DescriptorFactory.findAll(TraceDistance.class);

        }

        public static TraceDistance find(String qualifiedName) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(TraceDistance.class, qualifiedName);
        }

        public static TraceDistance find(String qualifiedName, Aggregator aggregator) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(TraceDistance.class, qualifiedName, aggregator);
        }

        public static TraceDistance getDefault() throws DescriptorFactory.InstanceNotFountException {
            return find(DefaultTraceDistance.class.getName());
        }
    }
}