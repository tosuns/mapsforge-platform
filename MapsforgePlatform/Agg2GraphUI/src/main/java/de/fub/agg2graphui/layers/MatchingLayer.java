/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author Serdar
 */
public class MatchingLayer extends AbstractLayer<List<? extends ILocation>> {

    public MatchingLayer() {
        super("Matching Layer", "Matching Layer", new RenderingOptions());
        getRenderingOptions().setColor(new Color(232, 23, 79)); // red
        getRenderingOptions().setzIndex(1);
        getRenderingOptions().setOpacity(0.7);
    }

    @Override
    protected void drawDrawables(Graphics2D graphics, Rectangle rectangle) {
        for (List<? extends ILocation> locationList : getItemList()) {
            ILocation lastLocation = null;
            for (ILocation location : locationList) {
                if (lastLocation != null) {
                    drawLine(lastLocation, location, null, getRenderingOptions(), 1, true);
                }
                drawPoint(location, getRenderingOptions());
                lastLocation = location;
            }
        }
    }
}
