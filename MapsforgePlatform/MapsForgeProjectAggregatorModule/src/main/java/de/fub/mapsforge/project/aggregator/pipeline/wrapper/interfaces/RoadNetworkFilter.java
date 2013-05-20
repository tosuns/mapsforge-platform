/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.roadgen.IRoadNetworkFilter;
import de.fub.mapsforge.project.aggregator.pipeline.wrapper.roadgeneration.DefaultRoadNetworkFilter;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface RoadNetworkFilter extends IRoadNetworkFilter, RoadNetworkDescriptor {

    public static class Factory {

        public static RoadNetworkFilter getDefault() throws InstanceNotFound {
            return find(DefaultRoadNetworkFilter.class.getName());
        }

        public static Collection<RoadNetworkFilter> findAll() {
            return RoadNetworkDescriptor.Factory.findAll(RoadNetworkFilter.class);
        }

        public static RoadNetworkFilter find(String qualifiedName) throws InstanceNotFound {
            RoadNetworkFilter roadNetworkFilter = RoadNetworkDescriptor.Factory.find(qualifiedName, RoadNetworkFilter.class);
            return roadNetworkFilter;
        }
    }
}
