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
@NbBundle.Messages({
    "CLT_OSMBusMapProvider_Name=Bus Map",
    "CLT_OSMBusMapProvider_Description=Provides a map which displayes the bus network."
})
@ServiceProvider(service = MapProvider.class)
public class OSMBusMapProvider implements MapProvider {

    private final OpenstreetMapService SERVICE = new OpenstreetMapService();

    @Override
    public String getName() {
        return Bundle.CLT_OSMBusMapProvider_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_OSMBusMapProvider_Description();
    }

    @Override
    public Osm getMap(double leftLon, double bottomLat, double rightLon, double topLat) {
        Osm result = SERVICE.getOSMBusMap(Osm.class,
                String.format(Locale.ENGLISH, "%f", leftLon),
                String.format(Locale.ENGLISH, "%f", bottomLat),
                String.format(Locale.ENGLISH, "%f", rightLon),
                String.format(Locale.ENGLISH, "%f", topLat));
        SERVICE.close();
        return result;
    }
}
