/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.mapsforge.project.models.Aggregator;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public interface Descriptor {

    public void setAggregator(Aggregator aggregator);

    public Aggregator getAggregator();

    public Node getNodeDelegate();
}
