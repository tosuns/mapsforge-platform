/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.map.provider;

import de.fub.mapsforgeplatform.openstreetmap.service.MapProvider;
import de.fub.mapsforgeplatform.openstreetmap.service.OpenstreetMapService;
import de.fub.mapsforgeplatform.openstreetmap.xml.osm.Osm;
import java.util.Locale;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = MapProvider.class)
public class OSMMapProvider implements MapProvider {

    private final OpenstreetMapService SERVICE = new OpenstreetMapService();

    @NbBundle.Messages({"CLT_OSMMapProvider_Name=OSM Map Provider"})
    @Override
    public String getName() {
        return Bundle.CLT_OSMMapProvider_Name();
    }

    @NbBundle.Messages({"CLT_OSMMapProvider_Description=Provides a map with all available data points"})
    @Override
    public String getDescription() {
        return Bundle.CLT_OSMMapProvider_Description();
    }

    @Override
    public Osm getMap(double leftLon, double bottomLat, double rightLon, double topLat) {
        Osm map = SERVICE.getOSMMap(Osm.class,
                String.format(Locale.ENGLISH, "%d", leftLon),
                String.format(Locale.ENGLISH, "%d", bottomLat),
                String.format(Locale.ENGLISH, "%d", rightLon),
                String.format(Locale.ENGLISH, "%d", topLat));
        SERVICE.close();
        return map;
    }
}
