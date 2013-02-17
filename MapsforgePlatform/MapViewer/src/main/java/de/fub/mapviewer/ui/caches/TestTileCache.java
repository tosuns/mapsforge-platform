/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui.caches;

import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 *
 * @author Serdar
 */
public class TestTileCache extends MemoryTileCache {

    @Override
    public Tile getTile(TileSource source, int x, int y, int z) {
        return super.getTile(source, x, y, z); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected CacheEntry createCacheEntry(Tile tile) {
        CacheEntry entry = super.createCacheEntry(tile);
        return entry; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTile(Tile tile) {
        super.addTile(tile); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeOldEntries() {
        super.removeOldEntries(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntry(CacheEntry entry) {
        super.removeEntry(entry); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        super.clear(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getTileCount() {
        return super.getTileCount(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCacheSize() {
        return super.getCacheSize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCacheSize(int cacheSize) {
        super.setCacheSize(cacheSize); //To change body of generated methods, choose Tools | Templates.
    }
}
