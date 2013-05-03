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
    private transient ProxyTileCache proxTileCache;

    public MapViewer() {
        this(new ProxyTileCache(new PersistentTileCache()), Runtime.getRuntime().availableProcessors() * 4);
//        init();

    }

    public MapViewer(TileCache tileCache, int downloadThreadCount) {
        this(new ProxyTileCache(tileCache), downloadThreadCount);
    }

    private MapViewer(ProxyTileCache proxTileCache, int downloadThreadCount) {
        super(proxTileCache, downloadThreadCount);
        this.proxTileCache = proxTileCache;
        init();
    }

    private void init() {
        DefaultMapController controller = new DefaultMapController(MapViewer.this);
        controller.setMovementMouseButton(MouseEvent.BUTTON1);
        setZoomContolsVisible(false);
    }

    protected void setTileCache(TileCache cache) {
        if (this.proxTileCache != null) {
            this.proxTileCache.setTileCache(cache);
        }
    }
}
