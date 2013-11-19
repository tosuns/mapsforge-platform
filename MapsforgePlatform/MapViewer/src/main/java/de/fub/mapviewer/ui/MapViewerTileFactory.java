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
        if (getName() != null && factory.getName() != null) {
            result = getName().compareTo(factory.getName());
        }
        return result;
    }

}
