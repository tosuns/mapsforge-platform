/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.roadgen.IRoadObjectMerger;
import de.fub.maps.project.aggregator.pipeline.wrapper.roadgeneration.DefaultRoadObjectMerger;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface RoadObjectMerger extends IRoadObjectMerger, RoadNetworkDescriptor {

    public static class Factory {

        public static RoadObjectMerger getDefault() throws InstanceNotFound {
            return find(DefaultRoadObjectMerger.class.getName());
        }

        public static Collection<RoadObjectMerger> findAll() {
            return RoadNetworkDescriptor.Factory.findAll(RoadObjectMerger.class);
        }

        public static RoadObjectMerger find(String qualifiedName) throws InstanceNotFound {
            return RoadNetworkDescriptor.Factory.find(qualifiedName, RoadObjectMerger.class);
        }
    }
}
