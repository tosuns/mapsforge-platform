/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.roadgen.Intersection;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Serdar
 */
public class IntersectionLayer extends AbstractLayer<Intersection> {

    public IntersectionLayer() {
        super("intersections", "Intersections", new RenderingOptions());
        getRenderingOptions().setColor(new Color(137, 0, 255)); // dark blue, semi transparent!
        getRenderingOptions().setRenderingType(RenderingOptions.RenderingType.POINTS);
        getRenderingOptions().setzIndex(4);
        getRenderingOptions().setOpacity(0.5);
        getRenderingOptions().setStrokeBaseWidthFactor(25);
    }

    @Override
    protected void drawDrawables(Graphics2D graphics, Rectangle rectangle) {
        // render intersections
        RenderingOptions intersectionRo = getRenderingOptions().getCopy();

        // compute a factor depending on the current zoom level to make the stroke width
        // more adaptive.
        float maxZoom = getLayerManager().getMapViewer().getTileFactory().getInfo().getMaximumZoomLevel();
        float zoom = getLayerManager().getMapViewer().getZoom();
        // make sure divisor maxZoom != 0
        maxZoom = maxZoom == 0 ? 1 : maxZoom;
        float factor = 1;
        double loopCount = (maxZoom - zoom);

        for (int i = 0; i < loopCount; i++) {
            factor = (float) Math.pow(zoom / maxZoom, 2);
        }

        intersectionRo.setStrokeBaseWidthFactor((20f) * Math.max(factor, 0.1f));
        for (Intersection intersection : getItemList()) {
            if (intersection.isVisible()) {
                drawPoint(intersection, intersectionRo);
            }
        }
    }
}
