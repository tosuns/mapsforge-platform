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
package de.fub.agg2graphui;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Serdar
 */
public class MapViewListener extends MouseAdapter implements ComponentListener {

    private Point lastPoint = new Point();
    private final AggTopComponent aggTopComponent;

    public MapViewListener(AggTopComponent aggTopComponent) {
        this.aggTopComponent = aggTopComponent;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        lastPoint = e.getPoint();
        this.aggTopComponent.updateLonLat(lastPoint);
        this.aggTopComponent.updateBoundingBox();
        this.aggTopComponent.updateZoomLevel();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        lastPoint = e.getPoint();
        this.aggTopComponent.updateLonLat(lastPoint);
        this.aggTopComponent.updateBoundingBox();
        this.aggTopComponent.getLayerManager().requestUpdate();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastPoint = e.getPoint();
        this.aggTopComponent.updateLonLat(lastPoint);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.aggTopComponent.updateBoundingBox();
        this.aggTopComponent.updateLonLat(new Point(e.getComponent().getWidth() / 2, e.getComponent().getHeight() / 2));
        this.aggTopComponent.updateZoomLevel();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
