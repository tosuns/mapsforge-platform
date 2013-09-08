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
package de.fub.maps.gpx.analysis.models.factories;

import de.fub.gpxmodule.xml.Trk;
import de.fub.maps.gpx.analysis.models.nodes.TrackNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class TrackNodeFactory extends ChildFactory<Trk> {

    private final List<Trk> trackList;

    public TrackNodeFactory(List<Trk> trackList) {
        this.trackList = trackList;
    }

    @Override
    protected boolean createKeys(List<Trk> toPopulate) {
        toPopulate.addAll(trackList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Trk track) {
        return new TrackNode(track);
    }
}
