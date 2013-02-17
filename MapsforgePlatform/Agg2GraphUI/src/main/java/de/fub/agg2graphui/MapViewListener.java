/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        lastPoint = e.getPoint();
        this.aggTopComponent.updateLonLat(lastPoint);
        this.aggTopComponent.updateBoundingBox();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastPoint = e.getPoint();
        this.aggTopComponent.updateLonLat(lastPoint);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.aggTopComponent.updateBoundingBox();
        try {
            this.aggTopComponent.updateLonLat(new Point(e.getComponent().getWidth() / 2, e.getComponent().getHeight() / 2));
        } catch (Exception ex) {
            this.aggTopComponent.updateLonLat(lastPoint);
        }
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
