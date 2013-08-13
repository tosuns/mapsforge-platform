/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
