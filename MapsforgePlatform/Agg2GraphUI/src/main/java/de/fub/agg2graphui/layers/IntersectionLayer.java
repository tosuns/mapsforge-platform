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
import java.util.List;

/**
 *
 * @author Serdar
 */
public class IntersectionLayer extends AbstractLayer<Intersection> {

    public IntersectionLayer() {
        super("intersections", "Intersections", new RenderingOptions());
        getOptions().setColor(new Color(137, 0, 255)); // dark blue, semi transparent!
        getOptions().setRenderingType(RenderingOptions.RenderingType.POINTS);
        getOptions().setzIndex(4);
        getOptions().setOpacity(0.5);
        getOptions().setStrokeBaseWidthFactor(25);
    }

    @Override
    protected void drawDrawables(List<Drawable> drawables, Graphics2D graphics, Rectangle rectangle) {
        // render intersections
        RenderingOptions intersectionRo = getOptions().getCopy();
        intersectionRo.setStrokeBaseWidthFactor(getLayerManager().getSize().width / 50);
        for (Intersection intersection : getItemList()) {
            if (intersection.isVisible()) {
                drawPoint(intersection, intersectionRo);
            }
        }
    }
}
