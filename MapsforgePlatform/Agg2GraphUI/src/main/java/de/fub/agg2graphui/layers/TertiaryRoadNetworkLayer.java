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
public class TertiaryRoadNetworkLayer extends RoadNetworkLayer {

    public TertiaryRoadNetworkLayer() {
        super("Teritary Roads", "Displays teritary roads of the road network.");
        setRoadType(Road.RoadType.TERTIARY);
        getRenderingOptions().setColor(new Color(221, 255, 68));
    }
}
