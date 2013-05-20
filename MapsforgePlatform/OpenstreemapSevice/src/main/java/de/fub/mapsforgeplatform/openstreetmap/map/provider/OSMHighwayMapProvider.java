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
public class OSMHighwayMapProvider implements MapProvider {

    private final OpenstreetMapService SERVICE = new OpenstreetMapService();

    @NbBundle.Messages("OSMHighwayMapProvider_Name=OSM Highway Map Provider")
    @Override
    public String getName() {
        return Bundle.OSMHighwayMapProvider_Name();
    }

    @NbBundle.Messages("OSMHighwayMapProvider_Description=Provides OSM Maps with only Highway, i.e. plain roads.")
    @Override
    public String getDescription() {
        return Bundle.OSMHighwayMapProvider_Description();
    }

    @Override
    public Osm getMap(double leftLon, double bottomLat, double rightLon, double topLat) {
        Osm map = SERVICE.getOSMHighwayMap(Osm.class,
                String.format(Locale.ENGLISH, "%f", leftLon),
                String.format(Locale.ENGLISH, "%f", bottomLat),
                String.format(Locale.ENGLISH, "%f", rightLon),
                String.format(Locale.ENGLISH, "%f", topLat));
        SERVICE.close();
        return map;
    }
}
