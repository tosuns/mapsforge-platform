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

import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.maps.project.aggregator.pipeline.wrapper.DefaultCachingStrategy;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.models.Aggregator;
import java.util.Collection;

/**
 *
 * @author Serdar
 */
public interface CachingStrategy extends ICachingStrategy, Descriptor {

    public PropertySection getPropertySection();

    public static class Factory {

        public static Collection<? extends CachingStrategy> findAll() {
            return DescriptorFactory.findAll(CachingStrategy.class);
        }

        public static CachingStrategy find(String qualifiedName) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(CachingStrategy.class, qualifiedName);
        }

        public static CachingStrategy find(String qualifiedName, Aggregator aggregator) throws DescriptorFactory.InstanceNotFountException {
            return DescriptorFactory.find(CachingStrategy.class, qualifiedName, aggregator);
        }

        public static CachingStrategy getDefault() throws DescriptorFactory.InstanceNotFountException {
            return find(DefaultCachingStrategy.class.getName());
        }
    }
}
