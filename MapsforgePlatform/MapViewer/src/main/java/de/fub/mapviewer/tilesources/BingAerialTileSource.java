/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.tilesources;

import org.openide.util.lookup.ServiceProvider;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 * The propose of this class is to register the openstreetmap
 * BingAerialTileSouce type via
 * <code>@ServiceProvider</code>.
 *
 * @author Serdar
 */
@ServiceProvider(service = TileSource.class)
public class BingAerialTileSource extends org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource {
}
