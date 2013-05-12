/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.roadgen.Road;
import java.awt.Color;

/**
 *
 * @author Serdar
 */
public class SecondaryRoadNetworkLayer extends RoadNetworkLayer {

    public SecondaryRoadNetworkLayer() {
        super("Secondary Roads", "Displays secondary roads of the road network.");
        setRoadType(Road.RoadType.SECONDARY);
        getRenderingOptions().setColor(new Color(253, 143, 0));
    }
}
