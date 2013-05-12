/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author Serdar
 */
public class MapMatchingLayer extends AbstractLayer<Line> {

    public MapMatchingLayer(String name, RenderingOptions renderingOptions) {
        super(name, name, renderingOptions);
    }

    @Override
    protected void drawDrawables(Graphics2D graphics, Rectangle rectangle) {
        List<Line> itemList = getItemList();
        for (Line line : itemList) {
            line.setRenderingOptions(getRenderingOptions());
            drawLine(line.getFrom(), line.getTo(), line.getLabel(), line.getRenderingOptions());
        }
    }
}
