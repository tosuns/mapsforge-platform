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

import de.fub.gpxmodule.xml.Trkseg;
import de.fub.maps.gpx.analysis.models.nodes.TrackSegmentNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class TrackSegmentNodeFactory extends ChildFactory<Trkseg> {

    private final List<Trkseg> trackSegmentList;

    public TrackSegmentNodeFactory(List<Trkseg> trackSegementList) {
        this.trackSegmentList = trackSegementList;
    }

    @Override
    protected boolean createKeys(List<Trkseg> toPopulate) {
        toPopulate.addAll(this.trackSegmentList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Trkseg trackSegment) {
        return new TrackSegmentNode(trackSegment);
    }
}
