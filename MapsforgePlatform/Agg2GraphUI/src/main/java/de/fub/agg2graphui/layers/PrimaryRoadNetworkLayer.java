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
public class PrimaryRoadNetworkLayer extends RoadNetworkLayer {

    public PrimaryRoadNetworkLayer() {
        super("Primary Roads", "Displays primary roads of the road network.");
        setRoadType(Road.RoadType.PRIMARY);
        getRenderingOptions().setColor(new Color(219, 37, 37));
    }
}
