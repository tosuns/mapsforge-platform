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
package de.fub.maps.gpx.analysis.models.nodes;

import de.fub.gpxmodule.xml.Trkseg;
import de.fub.maps.gpx.analysis.models.GpxTrackSegmentStatistic;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class TrackSegmentNode extends CustomNode {

    public TrackSegmentNode(Trkseg trackSegment) {
        super(Children.LEAF, Lookups.fixed(new GpxTrackSegmentStatistic(trackSegment)));
        setDisplayName("Track Segment");
    }
}
