/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

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
