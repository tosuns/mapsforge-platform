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
package de.fub.maps.project.models;

import de.fub.maps.project.aggregator.xml.Source;
import java.text.MessageFormat;

/**
 *
 * @author Serdar
 */
public class AggregatorSource {

    private final Aggregator aggregator;
    private final Source source;

    public AggregatorSource(Aggregator aggregator, Source source) {
        this.aggregator = aggregator;
        this.source = source;
    }

    public Aggregator getAggregator() {
        return aggregator;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.aggregator != null ? this.aggregator.hashCode() : 0);
        hash = 53 * hash + (this.source != null ? this.source.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AggregatorSource other = (AggregatorSource) obj;
        if (this.aggregator != other.aggregator && (this.aggregator == null || !this.aggregator.equals(other.aggregator))) {
            return false;
        }
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return MessageFormat.format("AggregatorSource{aggregator={0}, source={1}{2}", aggregator, source, '}');
    }
}
