/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.gpx.analysis.models.factories;

import de.fub.mapsforge.gpx.analysis.models.nodes.TrackSegmentNode;
import de.fub.gpxmodule.xml.Trkseg;
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
