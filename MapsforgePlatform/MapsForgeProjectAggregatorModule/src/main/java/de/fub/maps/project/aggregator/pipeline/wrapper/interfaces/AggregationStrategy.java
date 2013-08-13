/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.maps.project.aggregator.pipeline.wrapper.aggregation.strategy.DefaultAggregationStrategy;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.models.Aggregator;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface AggregationStrategy extends IAggregationStrategy, Descriptor {

    public PropertySection getPropertySection();

    public static class Factory {

        public static Collection<? extends AggregationStrategy> findAll() {
            return DescriptorFactory.findAll(AggregationStrategy.class);
        }

        public static AggregationStrategy find(String qualifiedName) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(AggregationStrategy.class, qualifiedName);
        }

        public static AggregationStrategy find(String qualifiedName, Aggregator aggregator) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(AggregationStrategy.class, qualifiedName, aggregator);
        }

        public static AggregationStrategy getDefault() throws DescriptorFactory.InstanceNotFountException {
            return find(DefaultAggregationStrategy.class.getName());
        }
    }
}
