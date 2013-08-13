/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.openstreetmap.ui;

import de.fub.maps.project.openstreetmap.service.LocationBoundingBoxService;
import de.fub.mapviewer.shapes.OsmRectangle;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
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
public class MapViewerBoundingBoxProvider extends javax.swing.JPanel implements LocationBoundingBoxService {

    private static final long serialVersionUID = 1L;
    private transient final ChangeSupport csp = new ChangeSupport(this);
    private transient final MouseAdapter mouseAdapter = new MouseAdapterImpl();
    private transient ComponentListener componentListener = new ComponentAdapterImpl();

    /**
     * Creates new form MapViewer
     */
    public MapViewerBoundingBoxProvider() {
        super();
        initComponents();
        mapViewer.addMouseListener(mouseAdapter);
        mapViewer.addMouseMotionListener(mouseAdapter);
        mapViewer.addMouseWheelListener(mouseAdapter);
        mapViewer.addComponentListener(componentListener);
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
        mapViewer.setDisplayToFitMapRectangles();
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
        ArrayList<MapMarker> list = new ArrayList<MapMarker>(mapViewer.getMapMarkerList());
        for (MapMarker marker : list) {
            removeMapMarker(marker);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        downloadButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        leftLong = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        jLabel2 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        bottomLat = new javax.swing.JTextField();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        jLabel3 = new javax.swing.JLabel();
        rightLong = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        jLabel4 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        topLat = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        jLabel5 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        pageStart = new javax.swing.JSpinner();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jLabel6 = new javax.swing.JLabel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        pageEnd = new javax.swing.JSpinner();
        statusBar = new javax.swing.JPanel();
        mapViewer = new de.fub.mapviewer.ui.MapViewer();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        downloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/fub/maps/project/openstreetmap/ui/downloadIcon.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(downloadButton, org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.downloadButton.text")); // NOI18N
        downloadButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jToolBar1.add(downloadButton);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(filler8);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.jLabel1.text")); // NOI18N
        jLabel1.setMinimumSize(new java.awt.Dimension(80, 14));
        jToolBar1.add(jLabel1);
        jToolBar1.add(filler2);

        leftLong.setText(org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.leftLong.text")); // NOI18N
        leftLong.setMinimumSize(new java.awt.Dimension(80, 20));
        leftLong.setPreferredSize(new java.awt.Dimension(30, 20));
        leftLong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftLongActionPerformed(evt);
            }
        });
        jToolBar1.add(leftLong);
        jToolBar1.add(filler1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.jLabel2.text")); // NOI18N
        jToolBar1.add(jLabel2);
        jToolBar1.add(filler3);

        bottomLat.setText(org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.bottomLat.text")); // NOI18N
        bottomLat.setMinimumSize(new java.awt.Dimension(80, 20));
        bottomLat.setPreferredSize(new java.awt.Dimension(30, 20));
        bottomLat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bottomLatActionPerformed(evt);
            }
        });
        jToolBar1.add(bottomLat);
        jToolBar1.add(filler4);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.jLabel3.text")); // NOI18N
        jLabel3.setMinimumSize(new java.awt.Dimension(80, 14));
        jToolBar1.add(jLabel3);

        rightLong.setText(org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.rightLong.text")); // NOI18N
        rightLong.setMinimumSize(new java.awt.Dimension(80, 20));
        rightLong.setPreferredSize(new java.awt.Dimension(30, 20));
        rightLong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightLongActionPerformed(evt);
            }
        });
        jToolBar1.add(rightLong);
        jToolBar1.add(filler5);
        jToolBar1.add(filler6);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.jLabel4.text")); // NOI18N
        jLabel4.setMinimumSize(new java.awt.Dimension(80, 14));
        jToolBar1.add(jLabel4);
        jToolBar1.add(filler7);

        topLat.setText(org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.topLat.text")); // NOI18N
        topLat.setMinimumSize(new java.awt.Dimension(80, 20));
        topLat.setPreferredSize(new java.awt.Dimension(30, 20));
        topLat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topLatActionPerformed(evt);
            }
        });
        jToolBar1.add(topLat);
        jToolBar1.add(jSeparator2);
        jToolBar1.add(filler10);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.jLabel5.text")); // NOI18N
        jLabel5.setToolTipText(org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.jLabel5.toolTipText")); // NOI18N
        jLabel5.setMinimumSize(new java.awt.Dimension(80, 14));
        jToolBar1.add(jLabel5);
        jToolBar1.add(filler9);

        pageStart.setMinimumSize(new java.awt.Dimension(30, 20));
        pageStart.setPreferredSize(new java.awt.Dimension(50, 20));
        jToolBar1.add(pageStart);
        jToolBar1.add(filler11);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(MapViewerBoundingBoxProvider.class, "MapViewerBoundingBoxProvider.jLabel6.text")); // NOI18N
        jToolBar1.add(jLabel6);
        jToolBar1.add(filler12);

        pageEnd.setPreferredSize(new java.awt.Dimension(50, 20));
        jToolBar1.add(pageEnd);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        statusBar.setPreferredSize(new java.awt.Dimension(100, 21));
        statusBar.setLayout(new java.awt.BorderLayout());
        add(statusBar, java.awt.BorderLayout.PAGE_END);

        mapViewer.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(153, 153, 153)));
        add(mapViewer, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void leftLongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftLongActionPerformed
        updateView();
    }//GEN-LAST:event_leftLongActionPerformed

    private void bottomLatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bottomLatActionPerformed
        updateView();
    }//GEN-LAST:event_bottomLatActionPerformed

    private void rightLongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightLongActionPerformed
        updateView();
    }//GEN-LAST:event_rightLongActionPerformed

    private void topLatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topLatActionPerformed
        updateView();
    }//GEN-LAST:event_topLatActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bottomLat;
    private javax.swing.JButton downloadButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField leftLong;
    private de.fub.mapviewer.ui.MapViewer mapViewer;
    private javax.swing.JSpinner pageEnd;
    private javax.swing.JSpinner pageStart;
    private javax.swing.JTextField rightLong;
    private javax.swing.JPanel statusBar;
    private javax.swing.JTextField topLat;
    // End of variables declaration//GEN-END:variables

    @Override
    public BoundingBox getViewBoundingBox() {
        Coordinate topLect = mapViewer.getPosition(getLocation().y, getLocation().x);
        Coordinate bottomRight = mapViewer.getPosition(getLocation().y + getHeight(),
                getLocation().x + getWidth());
        return new BoundingBox(topLect.getLat(), topLect.getLon(), bottomRight.getLat(), bottomRight.getLon());
    }

    public JButton getDownloadButton() {
        return downloadButton;
    }

    public JSpinner getPageEnd() {
        return pageEnd;
    }

    public JSpinner getPageStart() {
        return pageStart;
    }

    private void fireChangeEvent() {
        String formatString = "{0, number, 000.########}°";
        BoundingBox viewBoundingBox = getViewBoundingBox();
        leftLong.setText(MessageFormat.format(formatString, viewBoundingBox.getLeftLongitude()));
        bottomLat.setText(MessageFormat.format(formatString, viewBoundingBox.getBottomLatitude()));
        rightLong.setText(MessageFormat.format(formatString, viewBoundingBox.getRightLongitude()));
        topLat.setText(MessageFormat.format(formatString, viewBoundingBox.getTopLatitude()));
        csp.fireChange();
    }

    public void setProgressBar(Component component) {
        synchronized (getTreeLock()) {
            statusBar.removeAll();
            if (component != null) {
                statusBar.add(component, BorderLayout.CENTER);
            }
            statusBar.revalidate();
            repaint();
        }
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        csp.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        csp.removeChangeListener(listener);
    }

    private void updateView() {
        double left = Double.parseDouble(leftLong.getText().replaceAll("°", ""));
        double top = Double.parseDouble(topLat.getText().replaceAll("°", ""));
        double right = Double.parseDouble(rightLong.getText().replaceAll("°", ""));
        double bottom = Double.parseDouble(bottomLat.getText().replaceAll("°", ""));

        mapViewer.setMapRectangleList(new ArrayList<MapRectangle>(Arrays.asList(new OsmRectangle(left, top, right, bottom))));
        mapViewer.setDisplayToFitMapRectangles();
        fireChangeEvent();
    }

    public void lockInputFields(boolean lock) {
        synchronized (getTreeLock()) {
            downloadButton.setEnabled(!lock);
            pageStart.setEnabled(!lock);
            pageEnd.setEnabled(!lock);
            leftLong.setEnabled(!lock);
            topLat.setEnabled(!lock);
            rightLong.setEnabled(!lock);
            bottomLat.setEnabled(!lock);
        }
    }

    private class MouseAdapterImpl extends MouseAdapter {

        public MouseAdapterImpl() {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            fireChangeEvent();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            fireChangeEvent();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            fireChangeEvent();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            fireChangeEvent();
        }
    }

    private class ComponentAdapterImpl extends ComponentAdapter {

        public ComponentAdapterImpl() {
        }

        @Override
        public void componentResized(ComponentEvent e) {
            fireChangeEvent();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            fireChangeEvent();
        }
    }
}
