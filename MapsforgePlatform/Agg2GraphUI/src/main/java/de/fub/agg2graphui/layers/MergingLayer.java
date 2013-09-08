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
public class MergingLayer extends AbstractLayer<List<? extends ILocation>> {

    public MergingLayer() {
        super("Merge Layer", "Merge layer", new RenderingOptions());
        getRenderingOptions().setColor(new Color(240, 225, 17)); // yellow/orange
        getRenderingOptions().setzIndex(2);
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
