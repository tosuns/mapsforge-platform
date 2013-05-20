/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.roadgen.IAggFilter;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.roadgeneration.DefaultRoadAggregationFilter;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface RoadAggregationFilter extends IAggFilter, RoadNetworkDescriptor {

    public static class Factory {

        public static RoadAggregationFilter getDefault() throws InstanceNotFound {
            return find(DefaultRoadAggregationFilter.class.getName());
        }

        public static Collection<RoadAggregationFilter> findAll() {
            return RoadNetworkDescriptor.Factory.findAll(RoadAggregationFilter.class);
        }

        public static RoadAggregationFilter find(String qualifiedName) throws InstanceNotFound {
            return RoadNetworkDescriptor.Factory.find(qualifiedName, RoadAggregationFilter.class);
        }
    }
}
