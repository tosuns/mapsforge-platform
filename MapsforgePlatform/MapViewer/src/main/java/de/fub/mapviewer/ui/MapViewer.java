/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui;

import de.fub.mapviewer.ui.caches.PersistentTileCache;
import de.fub.mapviewer.ui.caches.ProxyTileCache;
import java.awt.event.MouseEvent;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;

/**
 *
 * @author Serdar
 */
public class MapViewer extends JMapViewer {

    private static final long serialVersionUID = 1L;
    private transient final ProxyTileCache proxTileCache;

    public MapViewer() {
        this(new ProxyTileCache(new PersistentTileCache()), Runtime.getRuntime().availableProcessors() * 4);
    }

    public MapViewer(TileCache tileCache, int downloadThreadCount) {
        this(new ProxyTileCache(tileCache), downloadThreadCount);
    }

    private MapViewer(ProxyTileCache proxTileCache, int downloadThreadCount) {
        super(proxTileCache, downloadThreadCount);
        this.proxTileCache = proxTileCache;
        DefaultMapController controller = new DefaultMapController(MapViewer.this);
        controller.setMovementMouseButton(MouseEvent.BUTTON1);
        setZoomContolsVisible(false);
    }

    protected void setTileCache(TileCache cache) {
        this.proxTileCache.setTileCache(cache);
    }
}
