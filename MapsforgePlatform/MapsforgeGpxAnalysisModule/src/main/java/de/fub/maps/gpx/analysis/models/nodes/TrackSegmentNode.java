/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
