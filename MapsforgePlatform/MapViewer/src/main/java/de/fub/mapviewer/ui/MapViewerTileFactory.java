/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui;

import de.fub.mapviewer.ui.caches.TileCacheFactory;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 *
 * @author Serdar
 */
public class MapViewerTileFactory extends DefaultTileFactory implements Comparable<MapViewerTileFactory> {

    public MapViewerTileFactory(TileFactoryInfo info) {
        super(info);
        setTileCache(TileCacheFactory.createTileCache(MapViewerTileFactory.this));
    }

    public String getName() {
        return getInfo() != null ? getInfo().getName() : (getClass().getName() + "@" + hashCode());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(MapViewerTileFactory factory) {
        int result = 1;
        if (getName() == null) {
            result = 1;
        } else if (factory.getName() == null) {
            result = 1;
        } else {
            result = getName().compareTo(factory.getName());
        }
        return result;
    }

}
