/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui.caches;

import java.text.MessageFormat;
import java.util.HashMap;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 *
 * @author Serdar
 */
public class ProxyTileCache implements TileCache {

    private TileCache tileCache = null;
    private HashMap<String, Tile> buffer = new HashMap<String, Tile>();

    public ProxyTileCache() {
    }

    public ProxyTileCache(TileCache tileCache) {
        this.tileCache = tileCache;
    }

    public TileCache getTileCache() {
        return tileCache;
    }

    public void setTileCache(TileCache tileCache) {
        this.tileCache = tileCache;
        if (tileCache != null) {
            for (Tile tile : buffer.values()) {
                this.tileCache.addTile(tile);
            }
            buffer.clear();
        }
    }

    @Override
    public Tile getTile(TileSource ts, int i, int i1, int i2) {
        return tileCache != null ? tileCache.getTile(ts, i, i1, i2) : buffer.get(MessageFormat.format("{0}/{1}/{2}", i, i1, i2));
    }

    @Override
    public void addTile(Tile tile) {
        if (tileCache != null) {
            tileCache.addTile(tile);
        } else {
            buffer.put(MessageFormat.format("{0}/{1}/{2}", tile.getXtile(), tile.getYtile(), tile.getZoom()), tile);
        }
    }

    @Override
    public int getTileCount() {
        return tileCache != null ? tileCache.getTileCount() : buffer.size();
    }
}
