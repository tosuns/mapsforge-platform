/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author Serdar
 */
public class LocationLayer extends AbstractLayer<ILocation> {

    public LocationLayer(String name, RenderingOptions renderingOptions) {
        super(name, renderingOptions);
    }

    public LocationLayer(String name, String description, RenderingOptions renderingOptions) {
        super(name, description, renderingOptions);
    }

    @Override
    protected void drawDrawables(Graphics2D g2, Rectangle rectangle) {
        ILocation lastLocation = null;
        for (ILocation location : getItemList()) {
            if (lastLocation != null) {
                drawLine(lastLocation, location, getRenderingOptions(), 1);
            }
            drawPoint(location, getRenderingOptions());
            lastLocation = location;
        }
    }
}
