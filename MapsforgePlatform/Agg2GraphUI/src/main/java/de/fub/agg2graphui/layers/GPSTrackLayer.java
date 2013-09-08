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

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Serdar
 */
public class GPSTrackLayer extends AbstractLayer<GPSTrack> {

    public GPSTrackLayer(String name, RenderingOptions renderingOptions) {
        super(name, renderingOptions);
    }

    public GPSTrackLayer(String name, String description, RenderingOptions renderingOptions) {
        super(name, description, renderingOptions);
    }

    @Override
    protected void drawDrawables(Graphics2D graphics, Rectangle rectangle) {
        for (GPSTrack gpst : getItemList()) {
            for (GPSSegment segment : gpst) {
                GPSPoint lastPoint = null;
                for (GPSPoint point : segment) {
                    if (lastPoint != null) {
                        drawLine(lastPoint, point, getRenderingOptions());
                    }
                    drawPoint(point, getRenderingOptions());
                    lastPoint = point;
                }
            }
        }
    }
}
