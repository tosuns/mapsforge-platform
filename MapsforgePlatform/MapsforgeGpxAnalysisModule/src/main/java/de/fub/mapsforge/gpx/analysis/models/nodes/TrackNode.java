/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.gpx.analysis.models.nodes;

import de.fub.gpxmodule.xml.Trk;
import de.fub.mapsforge.gpx.analysis.models.factories.TrackSegmentNodeFactory;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import org.openide.nodes.Children;

/**
 *
 * @author Serdar
 */
public class TrackNode extends CustomNode {

    public TrackNode(final Trk trk) {
        super(Children.createLazy(new Callable<Children>() {
            @Override
            public Children call() throws Exception {
                return trk.getTrkseg().isEmpty() ? Children.LEAF : Children.create(new TrackSegmentNodeFactory(trk.getTrkseg()), true);
            }
        }));
        setDisplayName(MessageFormat.format("Name: {0}, Description: {1}", trk.getName(), trk.getDesc()));
    }
}
