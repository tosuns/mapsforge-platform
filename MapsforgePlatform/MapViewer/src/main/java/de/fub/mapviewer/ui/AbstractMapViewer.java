/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.MessageFormat;
import java.util.List;
import org.openide.util.NbBundle;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 *
 * @author Serdar
 */
public class AbstractMapViewer extends javax.swing.JPanel {

    private MapViewListener mouseListener = new MapViewListener();

    /**
     * Creates new form AbstractMapViewer
     */
    public AbstractMapViewer() {
        initComponents();
        mapViewer.addMouseListener(mouseListener);
        mapViewer.addMouseMotionListener(mouseListener);
        mapViewer.addMouseWheelListener(mouseListener);
        mapViewer.addComponentListener(mouseListener);
        mapViewer.setZoomContolsVisible(false);
    }

    public MapViewer getMapViewer() {
        return mapViewer;
    }

    public AbstractMapViewer(TileCache cache) {
        this();
    }

    protected void setTileCache(TileCache cache) {
        mapViewer.setTileCache(cache);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        return mapViewer.getToolTipText(event);
    }

    public void setDisplayPositionByLatLon(double lat, double lon, int zoom) {
        mapViewer.setDisplayPositionByLatLon(lat, lon, zoom);
    }

    public void setDisplayPositionByLatLon(Point mapPoint, double lat, double lon, int zoom) {
        mapViewer.setDisplayPositionByLatLon(mapPoint, lat, lon, zoom);
    }

    public void setDisplayPosition(int x, int y, int zoom) {
        mapViewer.setDisplayPosition(x, y, zoom);
    }

    public void setDisplayPosition(Point mapPoint, int x, int y, int zoom) {
        mapViewer.setDisplayPosition(mapPoint, x, y, zoom);
    }

    public void setDisplayToFitMapMarkers() {
        mapViewer.setDisplayToFitMapMarkers();
    }

    public void setDisplayToFitMapRectangle() {
        mapViewer.setDisplayToFitMapRectangle();
    }

    public Coordinate getPosition() {
        return mapViewer.getPosition();
    }

    public Coordinate getPosition(Point mapPoint) {
        return mapViewer.getPosition(mapPoint);
    }

    public Coordinate getPosition(int mapPointX, int mapPointY) {
        return mapViewer.getPosition(mapPointX, mapPointY);
    }

    public Point getMapPosition(double lat, double lon, boolean checkOutside) {
        return mapViewer.getMapPosition(lat, lon, checkOutside);
    }

    public Point getMapPosition(double lat, double lon) {
        return mapViewer.getMapPosition(lat, lon);
    }

    public Point getMapPosition(Coordinate coord) {
        return mapViewer.getMapPosition(coord);
    }

    public Point getMapPosition(Coordinate coord, boolean checkOutside) {
        return mapViewer.getMapPosition(coord, checkOutside);
    }

    public void moveMap(int x, int y) {
        mapViewer.moveMap(x, y);
    }

    public int getZoom() {
        return mapViewer.getZoom();
    }

    public void zoomIn() {
        mapViewer.zoomIn();
    }

    public void zoomIn(Point mapPoint) {
        mapViewer.zoomIn(mapPoint);
    }

    public void zoomOut() {
        mapViewer.zoomOut();
    }

    public void zoomOut(Point mapPoint) {
        mapViewer.zoomOut(mapPoint);
    }

    public void setZoom(int zoom, Point mapPoint) {
        mapViewer.setZoom(zoom, mapPoint);
    }

    public void setZoom(int zoom) {
        mapViewer.setZoom(zoom);
    }

    public boolean isTileGridVisible() {
        return mapViewer.isTileGridVisible();
    }

    public void setTileGridVisible(boolean tileGridVisible) {
        mapViewer.setTileGridVisible(tileGridVisible);
    }

    public boolean getMapMarkersVisible() {
        return mapViewer.getMapMarkersVisible();
    }

    public void setMapMarkerVisible(boolean mapMarkersVisible) {
        mapViewer.setMapMarkerVisible(mapMarkersVisible);
    }

    public void setMapMarkerList(List<MapMarker> mapMarkerList) {
        mapViewer.setMapMarkerList(mapMarkerList);
    }

    public List<MapMarker> getMapMarkerList() {
        return mapViewer.getMapMarkerList();
    }

    public void setMapRectangleList(List<MapRectangle> mapRectangleList) {
        mapViewer.setMapRectangleList(mapRectangleList);
    }

    public List<MapRectangle> getMapRectangleList() {
        return mapViewer.getMapRectangleList();
    }

    public void addMapMarker(MapMarker marker) {
        mapViewer.addMapMarker(marker);
    }

    public void removeMapMarker(MapMarker marker) {
        mapViewer.removeMapMarker(marker);
    }

    public void addMapRectangle(MapRectangle rectangle) {
        mapViewer.addMapRectangle(rectangle);
    }

    public void removeMapRectangle(MapRectangle rectangle) {
        mapViewer.removeMapRectangle(rectangle);
    }

    public void setZoomContolsVisible(boolean visible) {
        mapViewer.setZoomContolsVisible(visible);
    }

    public boolean getZoomContolsVisible() {
        return mapViewer.getZoomContolsVisible();
    }

    public void setTileSource(TileSource tileSource) {
        mapViewer.setTileSource(tileSource);
    }

    public void tileLoadingFinished(Tile tile, boolean success) {
        mapViewer.tileLoadingFinished(tile, success);
    }

    public boolean isMapRectanglesVisible() {
        return mapViewer.isMapRectanglesVisible();
    }

    public void setMapRectanglesVisible(boolean mapRectanglesVisible) {
        mapViewer.setMapRectanglesVisible(mapRectanglesVisible);
    }

    public TileCache getTileCache() {
        return mapViewer.getTileCache();
    }

    public void setTileLoader(TileLoader loader) {
        mapViewer.setTileLoader(loader);
    }

    public void removeAllMapMarkers() {
        getMapMarkerList().clear();
        repaint();
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.jLabel1.text")); // NOI18N
        statusbar.add(jLabel1);
        statusbar.add(filler2);

        org.openide.awt.Mnemonics.setLocalizedText(latitude, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.latitude.text")); // NOI18N
        statusbar.add(latitude);
        statusbar.add(filler1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.jLabel2.text")); // NOI18N
        statusbar.add(jLabel2);
        statusbar.add(filler3);

        org.openide.awt.Mnemonics.setLocalizedText(longitude, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.longitude.text")); // NOI18N
        longitude.setMaximumSize(new java.awt.Dimension(150, 14));
        statusbar.add(longitude);
        statusbar.add(filler5);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(AbstractMapViewer.class, "AbstractMapViewer.jLabel3.text")); // NOI18N
        statusbar.add(jLabel3);
        statusbar.add(filler4);

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
        Coordinate position = mapViewer.getPosition(point);
        setLatitude(position.getLat());
        setLongitude(position.getLon());
    }

    private void updateBoundingBox() {
        Coordinate leftLongBottomLat = mapViewer.getPosition(getLocation().x, getLocation().y + getHeight());
        Coordinate rigtLongTopLat = mapViewer.getPosition(getLocation().x + getWidth(), getLocation().y);
        boundingBox.setText(MessageFormat.format(NbBundle.getMessage(this.getClass(), "AbstractMapViewer.boundingBox.text"), leftLongBottomLat.getLon(), rigtLongTopLat.getLat(), rigtLongTopLat.getLon(), leftLongBottomLat.getLat()));
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
            } catch (Exception ex) {
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
