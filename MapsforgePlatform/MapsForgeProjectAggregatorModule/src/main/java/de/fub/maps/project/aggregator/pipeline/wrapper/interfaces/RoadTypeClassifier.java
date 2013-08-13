/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.agg2graph.roadgen.IRoadTypeClassifier;
import de.fub.maps.project.aggregator.pipeline.wrapper.roadgeneration.DefaultRoadTypeClassifier;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface RoadTypeClassifier extends IRoadTypeClassifier, RoadNetworkDescriptor {

    public static class Factory {

        public static RoadTypeClassifier getDefault() throws InstanceNotFound {
            return find(DefaultRoadTypeClassifier.class.getName());
        }

        public static Collection<RoadTypeClassifier> findAll() {
            return RoadNetworkDescriptor.Factory.findAll(RoadTypeClassifier.class);
        }

        public static RoadTypeClassifier find(String qualifiedName) throws InstanceNotFound {
            return RoadNetworkDescriptor.Factory.find(qualifiedName, RoadTypeClassifier.class);
        }
    }
}
