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

import de.fub.agg2graph.structs.DoubleRect;
import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.agg2graphui.controller.LayerManager;
import de.fub.mapviewer.shapes.WaypointMarker;
import de.fub.utilsmodule.text.MessageFormatter;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Locale;
import javax.swing.JPanel;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@Messages({
    "CTL_AggAction=Agg",
    "CTL_AggTopComponent=Agg Window",
    "HINT_AggTopComponent=This is a Agg window"
})
public final class AggTopComponent extends JPanel {

    private static final long serialVersionUID = 1L;
    private final transient MapViewListener mouseListener = new MapViewListener(this);
    private final HashSet<Waypoint> hashSet = new HashSet<Waypoint>();

    public AggTopComponent() {
        initComponents();
        setName(Bundle.CTL_AggTopComponent());
        setToolTipText(Bundle.HINT_AggTopComponent());
        init();
    }

    private void init() {
        mapViewer.addMouseListener(mouseListener);
        mapViewer.addMouseMotionListener(mouseListener);
        mapViewer.addMouseWheelListener(mouseListener);
        mapViewer.addComponentListener(mouseListener);
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(hashSet);
        mapViewer.setOverlayPainter(waypointPainter);
    }

    public void showArea(DoubleRect area) {
        HashSet<GeoPosition> set = new HashSet<GeoPosition>();
        set.add(new GeoPosition(area.getMinX(), area.getMinY()));
        set.add(new GeoPosition(area.getWidth(), area.getHeight()));

        mapViewer.calculateZoomFrom(set);

        updateZoomLevel();
        updateBoundingBox();
    }

    public void setDisplayToFitMapMarkers() {
        mapViewer.setDisplayToFitMapMarkers();
        updateZoomLevel();
        updateBoundingBox();
        mapViewer.repaint();
    }

    public void setDisplayToFitMapRectangle() {
        setDisplayToFitMapMarkers();
    }

    public synchronized void addMapMarker(WaypointMarker marker) {
        mapViewer.addWaypoint(marker);
        setDisplayToFitMapMarkers();
    }

    public synchronized void removeMapMarker(WaypointMarker marker) {
        mapViewer.removeWaypoint(marker);
        setDisplayToFitMapMarkers();
    }

    public synchronized void removeAllMarkers() {
        hashSet.clear();
        setDisplayToFitMapMarkers();
    }

    public void updateLonLat(Point point) {
        GeoPosition position = mapViewer.convertPointToGeoPosition(point);
        setLatitude(position.getLatitude());
        setLongitude(position.getLongitude());
    }

    public void updateZoomLevel() {
        setZoomLevel(mapViewer.getZoom());
    }

    public void updateBoundingBox() {
        GeoPosition leftLongBottomLat = mapViewer.convertPointToGeoPosition(new Point2D.Double(getLocation().x, getLocation().y + getHeight()));
        GeoPosition rigtLongTopLat = mapViewer.convertPointToGeoPosition(new Point2D.Double(getLocation().x + getWidth(), getLocation().y));
        boundingBox.setText(MessageFormatter.format(Locale.ENGLISH,
                NbBundle.getMessage(this.getClass(), "AggTopComponent.boundingBox.text"),
                leftLongBottomLat.getLongitude(),
                rigtLongTopLat.getLatitude(),
                rigtLongTopLat.getLongitude(),
                leftLongBottomLat.getLatitude()));
    }

    public void setLatitude(double lat) {
        latitude.setText(MessageFormatter.format(Locale.ENGLISH,
                NbBundle.getMessage(this.getClass(), "AggTopComponent.latitude.text"), lat));
    }

    public void setLongitude(double lon) {
        longitude.setText(MessageFormatter.format(Locale.ENGLISH,
                NbBundle.getMessage(this.getClass(), "AggTopComponent.longitude.text"), lon).replaceAll(",", "\\."));
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel.setText(MessageFormatter.format(Locale.ENGLISH,
                NbBundle.getMessage(this.getClass(), "AggTopComponent.zoomLevel.text"), zoomLevel));
    }

    public void setLabelsVisible(boolean labelsVisible) {
        mapViewer.setLabelsVisible(labelsVisible);
    }

    public LayerManager getLayerManager() {
        return mapViewer.getLayerManager();
    }

    public void addLayer(AbstractLayer<?> layer) {
        mapViewer.addLayer(layer);
    }

    public void removeLayer(AbstractLayer<?> layer) {
        mapViewer.removeLayer(layer);
    }

    public void clear() {
        mapViewer.removeAllLayers();
    }

    public void setTileFactory(TileFactory tileFactory) {
        if (tileFactory.getInfo().getMaximumZoomLevel() < mapViewer.getZoom()) {
            mapViewer.setZoom(tileFactory.getInfo().getMaximumZoomLevel());
        }
        mapViewer.setTileFactory(tileFactory);
    }

    public TileFactory getTileFactory() {
        return mapViewer.getTileFactory();
    }

    public void setStatusBarVisible(boolean visible) {
        if (this.statusbar.isVisible() != visible) {
            this.statusbar.setVisible(visible);
        }
    }

    public boolean isStatusBarVisible() {
        return this.statusbar.isVisible();
    }

    /**
     *
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusbar = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        jLabel4 = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        zoomLevel = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(8, 222222));
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
        mapViewer = new de.fub.agg2graphui.AggContentPanel();

        setLayout(new java.awt.BorderLayout());

        statusbar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusbar.setLayout(new javax.swing.BoxLayout(statusbar, javax.swing.BoxLayout.X_AXIS));
        statusbar.add(filler6);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.jLabel4.text")); // NOI18N
        statusbar.add(jLabel4);
        statusbar.add(filler8);

        zoomLevel.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(zoomLevel, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.zoomLevel.text")); // NOI18N
        zoomLevel.setMaximumSize(new java.awt.Dimension(50, 14));
        zoomLevel.setMinimumSize(new java.awt.Dimension(50, 14));
        zoomLevel.setPreferredSize(new java.awt.Dimension(50, 14));
        statusbar.add(zoomLevel);
        statusbar.add(filler9);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.jLabel1.text")); // NOI18N
        statusbar.add(jLabel1);
        statusbar.add(filler2);

        latitude.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(latitude, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.latitude.text")); // NOI18N
        statusbar.add(latitude);
        statusbar.add(filler1);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.jLabel2.text")); // NOI18N
        statusbar.add(jLabel2);
        statusbar.add(filler3);

        longitude.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(longitude, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.longitude.text")); // NOI18N
        longitude.setMaximumSize(new java.awt.Dimension(150, 14));
        statusbar.add(longitude);
        statusbar.add(filler5);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.jLabel3.text")); // NOI18N
        statusbar.add(jLabel3);
        statusbar.add(filler4);

        boundingBox.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(boundingBox, org.openide.util.NbBundle.getMessage(AggTopComponent.class, "AggTopComponent.boundingBox.text")); // NOI18N
        boundingBox.setMaximumSize(new java.awt.Dimension(32665, 14));
        statusbar.add(boundingBox);
        statusbar.add(filler7);

        add(statusbar, java.awt.BorderLayout.SOUTH);

        mapViewer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        add(mapViewer, java.awt.BorderLayout.CENTER);
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
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel latitude;
    private javax.swing.JLabel longitude;
    private de.fub.agg2graphui.AggContentPanel mapViewer;
    private javax.swing.JPanel statusbar;
    private javax.swing.JLabel zoomLevel;
    // End of variables declaration//GEN-END:variables
}
