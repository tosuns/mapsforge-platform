/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.models;

import de.fub.maps.project.aggregator.xml.Source;
import java.text.MessageFormat;

/**
 *
 * @author Serdar
 */
public class AggregatorSource {

    private Aggregator aggregator;
    private Source source;

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
