/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.openstreetmap.map.provider;

import de.fub.maps.project.openstreetmap.service.MapProvider;
import de.fub.maps.project.openstreetmap.service.OpenstreetMapService;
import de.fub.maps.project.openstreetmap.xml.osm.Osm;
import java.util.Locale;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = MapProvider.class)
public class OSMTramMapProvider implements MapProvider {

    private final OpenstreetMapService SERVICE = new OpenstreetMapService();

    @NbBundle.Messages("OSMTramMapProvider_Name=OSM Tram railroad Map Provider")
    @Override
    public String getName() {
        return Bundle.OSMTramMapProvider_Name();
    }

    @NbBundle.Messages("OSMTramMapProvider_Description=Provides maps of tram railroads from OpenStreetMap")
    @Override
    public String getDescription() {
        return Bundle.OSMTramMapProvider_Description();
    }

    @Override
    public Osm getMap(double leftLon, double bottomLat, double rightLon, double topLat) {
        Osm map = SERVICE.getOSMTramMap(Osm.class,
                String.format(Locale.ENGLISH, "%f", leftLon),
                String.format(Locale.ENGLISH, "%f", bottomLat),
                String.format(Locale.ENGLISH, "%f", rightLon),
                String.format(Locale.ENGLISH, "%f", topLat));
        SERVICE.close();
        return map;
    }
}
