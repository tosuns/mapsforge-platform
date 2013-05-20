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
public class OSMSubwayRailRoadMapProvider implements MapProvider {

    private final OpenstreetMapService SERVICE = new OpenstreetMapService();

    @NbBundle.Messages("OSMSubwayRailRoadMapProvider_Name=OSM Subway railroad Map Provider")
    @Override
    public String getName() {
        return Bundle.OSMSubwayRailRoadMapProvider_Name();
    }

    @NbBundle.Messages("OSMSubwayRailRoadMapProvider_Description=Providers map of subway railroad from OpenStreetMap.")
    @Override
    public String getDescription() {
        return Bundle.OSMSubwayRailRoadMapProvider_Description();
    }

    @Override
    public Osm getMap(double leftLon, double bottomLat, double rightLon, double topLat) {
        Osm map = SERVICE.getOSMSubwayMap(Osm.class,
                String.format(Locale.ENGLISH, "%f", leftLon),
                String.format(Locale.ENGLISH, "%f", bottomLat),
                String.format(Locale.ENGLISH, "%f", rightLon),
                String.format(Locale.ENGLISH, "%f", topLat));
        SERVICE.close();
        return map;
    }
}
