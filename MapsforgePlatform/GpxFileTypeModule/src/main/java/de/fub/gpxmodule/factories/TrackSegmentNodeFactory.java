/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.factories;

import de.fub.gpxmodule.nodes.TrkSegNode;
import de.fub.gpxmodule.xml.gpx.Trkseg;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class TrackSegmentNodeFactory extends ChildFactory<Trkseg> {

    private final List<Trkseg> trkSegList;

    public TrackSegmentNodeFactory(List<Trkseg> list) {
        assert list != null;
        this.trkSegList = list;
    }

    @Override
    protected boolean createKeys(List<Trkseg> list) {
        list.addAll(this.trkSegList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Trkseg trkseg) {
        Node node = null;
        try {
            node = new TrkSegNode(trkseg, trkseg.getTrkpt() == null || trkseg.getTrkpt().isEmpty() ? Children.LEAF : Children.create(new TrkpointNodeFactory(trkseg.getTrkpt()), true));
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }
}
