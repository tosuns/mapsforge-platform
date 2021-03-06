/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class OSMRailRoadMapProvider implements MapProvider {

    private final OpenstreetMapService SERVICE = new OpenstreetMapService();

    @NbBundle.Messages("OSMRailRoadMapProvider_Name=OSM Railroad Map Provider")
    @Override
    public String getName() {
        return Bundle.OSMRailRoadMapProvider_Name();
    }

    @NbBundle.Messages("OSMRailRoadMapPRovider_Description=Provides Railroads from OpenStreetMap.")
    @Override
    public String getDescription() {
        return Bundle.OSMRailRoadMapPRovider_Description();
    }

    @Override
    public Osm getMap(double leftLon, double bottomLat, double rightLon, double topLat) {
        Osm map = SERVICE.getOSMTrainMap(Osm.class,
                String.format(Locale.ENGLISH, "%f", leftLon),
                String.format(Locale.ENGLISH, "%f", bottomLat),
                String.format(Locale.ENGLISH, "%f", rightLon),
                String.format(Locale.ENGLISH, "%f", topLat));
        SERVICE.close();
        return map;
    }
}
