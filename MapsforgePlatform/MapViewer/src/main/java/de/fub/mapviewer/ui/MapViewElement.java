/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.service.GPXProvider;
import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.gpxmodule.xml.gpx.Gpx.Trk;
import de.fub.gpxmodule.xml.gpx.Trkseg;
import de.fub.gpxmodule.xml.gpx.Wpt;
import java.awt.Color;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

/**
 *
 * @author Serdar
 */
@MultiViewElement.Registration(
        displayName = "#LBL_GPX_VISUAL",
        iconBase = "de/fub/gpxmodule/gpx.png",
        mimeType = "text/gpx+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "GPXVisual",
        position = 2000)
@NbBundle.Messages("LBL_GPX_VISUAL=Visual")
public class MapViewElement extends javax.swing.JPanel implements MultiViewElement, ChangeListener {

    private static final long serialVersionUID = 1L;
    private GPXDataObject obj;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;
    private GPXProvider gpxProvide;
    private boolean modelChanged = true;

    /**
     * Creates new form MapViewElement
     */
    public MapViewElement(Lookup lkp) {
        obj = lkp.lookup(GPXDataObject.class);
        assert obj != null;
        init();
    }

    private void init() {
        initComponents();
        gpxProvide = obj.getLookup().lookup(GPXProvider.class);
        gpxProvide.addChangeListener(this);
        update();
    }

    private void update() {
        abstractMapViewer1.removeAllMapMarkers();
        if (gpxProvide != null) {
            Gpx gpx = gpxProvide.getGpx();
            if (gpx != null) {

                for (Trk trk : gpx.getTrk()) {
                    for (Trkseg trkseg : trk.getValue().getTrkseg()) {
                        for (Wpt trkpt : trkseg.getTrkpt()) {
                            abstractMapViewer1.addMapMarker(
                                    new MapMarkerDot(
                                    Color.blue,
                                    trkpt.getLat().getValue().doubleValue(),
                                    trkpt.getLon().getValue().doubleValue()));
                        }
                    }
                }
                abstractMapViewer1.setDisplayToFitMapMarkers();
            }
        }
        modelChanged = false;
    }

    @Override
    public String getName() {
        return "GPXVisualElement";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        abstractMapViewer1 = new de.fub.mapviewer.ui.AbstractMapViewer();

        setLayout(new java.awt.BorderLayout());
        add(abstractMapViewer1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.mapviewer.ui.AbstractMapViewer abstractMapViewer1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return obj.getLookup();
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
        if (modelChanged) {
            update();
        }
        abstractMapViewer1.setDisplayToFitMapMarkers();
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() != this) {
            modelChanged = true;
        }
    }
}
