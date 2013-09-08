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

import de.fub.agg2graph.roadgen.IRoadNetworkFilter;
import de.fub.maps.project.aggregator.pipeline.wrapper.roadgeneration.DefaultRoadNetworkFilter;
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
