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
package de.fub.maps.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.ITraceDistance;
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

    public void setTraceDistance(ITraceDistance traceDistance);

    public void setBaseMergeHandler(IMergeHandler baseMergeHandler);

    /**
     * Factory to find <code>@ServiceProvider</code> annotated
     * AggregationStrategy implementations.
     */

    public static class Factory {

        /**
         * Finds all AggregationStrategy instances, which are annotated via
         * <code>@ServiceProvider</code>. This method delegates the call to the
         * {@code DescriptorFactory} class.
         *
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
