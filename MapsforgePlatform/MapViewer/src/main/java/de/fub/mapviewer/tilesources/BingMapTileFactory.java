/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.tilesources;

import de.fub.mapviewer.ui.MapViewerTileFactory;
import org.jdesktop.swingx.VirtualEarthTileFactoryInfo;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = MapViewerTileFactory.class)
public class BingMapTileFactory extends MapViewerTileFactory {

    public BingMapTileFactory() {
        super(new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP));
    }

    @Override
    public String getName() {
        return "Bing Map";
    }
}
