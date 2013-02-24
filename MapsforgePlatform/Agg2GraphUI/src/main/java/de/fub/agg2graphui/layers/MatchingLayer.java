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
        getOptions().setColor(new Color(232, 23, 79)); // red
        getOptions().setzIndex(1);
        getOptions().setOpacity(0.7);
    }

    @Override
    protected void drawDrawables(List<Drawable> drawables, Graphics2D graphics, Rectangle rectangle) {
        for (List<? extends ILocation> locationList : getItemList()) {
            ILocation lastLocation = null;
            for (ILocation location : locationList) {
                if (lastLocation != null) {
                    drawLine(lastLocation, location, getOptions(), 1, true);
                }
                drawPoint(location, getOptions());
                lastLocation = location;
            }
        }
    }
}
