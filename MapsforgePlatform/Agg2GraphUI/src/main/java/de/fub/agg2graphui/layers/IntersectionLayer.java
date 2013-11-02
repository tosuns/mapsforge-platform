/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        float zoom = maxZoom - getLayerManager().getMapViewer().getZoom();
        // make sure divisor maxZoom != 0
        maxZoom = maxZoom == 0 ? 1 : maxZoom;
        float factor = 1;

        factor = (float) Math.pow(zoom / maxZoom, 2);

        intersectionRo.setStrokeBaseWidthFactor((20f) * Math.max(factor, 0.1f));
        for (Intersection intersection : getItemList()) {
            if (intersection.isVisible()) {
                drawPoint(intersection, intersectionRo);
            }
        }
    }
}
