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
package de.fub.mapviewer.ui;

import de.fub.mapviewer.shapes.WaypointMarker;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.util.Set;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class AbstractMapViewer extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final transient MapViewListener mouseListener = new MapViewListener();

    /**
     * Creates new form AbstractMapViewer
     */
    public AbstractMapViewer() {
        initComponents();
        mapViewer.addMouseListener(mouseListener);
        mapViewer.addMouseMotionListener(mouseListener);
        mapViewer.addMouseWheelListener(mouseListener);
        mapViewer.addComponentListener(mouseListener);
    }

    public MapViewer getMapViewer() {
        return mapViewer;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        return mapViewer.getToolTipText(event);
    }

    public void setDisplayToFitMapMarkers() {
        mapViewer.setDisplayToFitMapMarkers();
    }

    public void setDisplayToFitMapRectangle() {
        setDisplayToFitMapMarkers();
    }

    public synchronized void addMapMarker(WaypointMarker marker) {
        mapViewer.addWaypoint(marker);
    }

    public synchronized void removeMapMarker(WaypointMarker marker) {
        mapViewer.removeWaypoint(marker);
    }

    public synchronized void removeAllMarkers() {
        mapViewer.clearWaypoints();
    }

    public void setDisplayPositionByLatLon(double lat, double lon) {
        mapViewer.setCenterPosition(new GeoPosition(lat, lon));
    }

    public GeoPosition getCenterPosition() {
        return mapViewer.getCenterPosition();
    }

    public GeoPosition getAddressLocation() {
        return mapViewer.getAddressLocation();
    }

    public void setAddressLocation(GeoPosition addressLocation) {
        mapViewer.setAddressLocation(addressLocation);
    }

    public void recenterToAddressLocation() {
        mapViewer.recenterToAddressLocation();
    }

    public void setLoadingImage(Image loadingImage) {
        mapViewer.setLoadingImage(loadingImage);
    }

    public void calculateZoomFrom(Set<GeoPosition> positions) {
        mapViewer.calculateZoomFrom(positions);
    }

    public void setRestrictOutsidePanning(boolean restrictOutsidePanning) {
        mapViewer.setRestrictOutsidePanning(restrictOutsidePanning);
    }

    public void setHorizontalWrapped(boolean horizontalWrapped) {
        mapViewer.setHorizontalWrapped(horizontalWrapped);
    }

    public Point2D convertGeoPositionToPoint(GeoPosition pos) {
        return mapViewer.convertGeoPositionToPoint(pos);
    }

    public GeoPosition convertPointToGeoPosition(Point2D pt) {
        return mapViewer.convertPointToGeoPosition(pt);
    }

    public int getZoom() {
        return mapViewer.getZoom();
    }

    public void setZoom(int zoom) {
        mapViewer.setZoom(zoom);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapViewer = new de.fub.mapviewer.ui.MapViewer();
        statusbar = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        latitude = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jLabel2 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        longitude = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        boundingBox = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));

        setLayout(new java.awt.BorderLayout());
        add(mapViewer, java.awt.BorderLayout.CENTER);

        statusbar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusbar.setLayout(new javax.swing.BoxLayout(statusbar, javax.swing.BoxLayout.X_AXIS));
        statusbar.add(filler6);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.jLabel1.text")); // NOI18N
        statusbar.add(jLabel1);
        statusbar.add(filler2);

        latitude.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(latitude, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.latitude.text")); // NOI18N
        statusbar.add(latitude);
        statusbar.add(filler1);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.jLabel2.text")); // NOI18N
        statusbar.add(jLabel2);
        statusbar.add(filler3);

        longitude.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(longitude, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.longitude.text")); // NOI18N
        longitude.setMaximumSize(new java.awt.Dimension(150, 14));
        statusbar.add(longitude);
        statusbar.add(filler5);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.jLabel3.text")); // NOI18N
        statusbar.add(jLabel3);
        statusbar.add(filler4);

        boundingBox.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(boundingBox, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.boundingBox.text")); // NOI18N
        boundingBox.setMaximumSize(new java.awt.Dimension(700, 14));
        boundingBox.setMinimumSize(new java.awt.Dimension(300, 14));
        statusbar.add(boundingBox);
        statusbar.add(filler7);

        add(statusbar, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel boundingBox;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel latitude;
    private javax.swing.JLabel longitude;
    private de.fub.mapviewer.ui.MapViewer mapViewer;
    private javax.swing.JPanel statusbar;
    // End of variables declaration//GEN-END:variables

    private void updateLonLat(Point point) {
        if (point != null) {
            GeoPosition position = convertPointToGeoPosition(point);
            setLatitude(position.getLatitude());
            setLongitude(position.getLongitude());
        }
    }

    private void updateBoundingBox() {
        GeoPosition leftLongBottomLat = convertPointToGeoPosition(new Point2D.Double(getLocation().x, getLocation().y + getHeight()));
        GeoPosition rigtLongTopLat = convertPointToGeoPosition(new Point2D.Double(getLocation().x + getWidth(), getLocation().y));
        boundingBox.setText(MessageFormat.format(NbBundle.getMessage(this.getClass(), "AbstractMapViewer.boundingBox.text"),
                leftLongBottomLat.getLongitude(),
                rigtLongTopLat.getLatitude(),
                rigtLongTopLat.getLongitude(),
                leftLongBottomLat.getLatitude()));
    }

    private void setLatitude(double lat) {
        latitude.setText(MessageFormat.format(NbBundle.getMessage(this.getClass(), "AbstractMapViewer.latitude.text"), lat));
    }

    private void setLongitude(double lon) {
        longitude.setText(MessageFormat.format(NbBundle.getMessage(this.getClass(), "AbstractMapViewer.longitude.text"), lon));
    }

    private class MapViewListener extends MouseAdapter implements ComponentListener {

        private Point lastPoint = new Point();

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            lastPoint = e.getPoint();
            updateLonLat(lastPoint);
            updateBoundingBox();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            lastPoint = e.getPoint();
            updateLonLat(lastPoint);
            updateBoundingBox();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            lastPoint = e.getPoint();
            updateLonLat(lastPoint);
            updateBoundingBox();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            lastPoint = e.getPoint();
            updateLonLat(lastPoint);
        }

        @Override
        public void componentResized(ComponentEvent e) {
            updateBoundingBox();
            try {
                updateLonLat(mapViewer.getMousePosition());
            } catch (HeadlessException ex) {
                updateLonLat(lastPoint);
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
}
