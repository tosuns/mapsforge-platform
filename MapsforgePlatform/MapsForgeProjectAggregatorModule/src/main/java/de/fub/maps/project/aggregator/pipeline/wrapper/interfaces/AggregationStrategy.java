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
 * Extends the IAggregator interface of the Agg2graph and the Descriptor
 * interface.
 *
 * @author Serdar
 */
public interface AggregationStrategy extends IAggregationStrategy, Descriptor {

    /**
     * Returns the PropertySection instance.
     *
     * @return PropertySection instance or null.
     */
    public PropertySection getPropertySection();

    /**
     * Factory to find
     * <code>@ServiceProvider</code> annotated AggregationStrategy
     * implementations.
     */
    public static class Factory {

        /**
         * Finds all AggregationStrategy instances, which are annotated via
         * <code>@ServiceProvider</code>. This method delegates the call to the
         * {@code DescriptorFactory} class.
         * @return A collection of all registered AggregationStrategy instances.
         */
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
