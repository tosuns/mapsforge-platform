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
