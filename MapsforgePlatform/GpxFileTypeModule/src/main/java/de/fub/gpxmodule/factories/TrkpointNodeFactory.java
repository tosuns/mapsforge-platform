/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.factories;

import de.fub.gpxmodule.nodes.TrkPointNode;
import de.fub.gpxmodule.xml.gpx.Wpt;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class TrkpointNodeFactory extends ChildFactory<Wpt> {

    private final List<Wpt> trkptList;

    public TrkpointNodeFactory(List<Wpt> list) {
        assert list != null;
        this.trkptList = list;
    }

    @Override
    protected boolean createKeys(List<Wpt> list) {
        list.addAll(this.trkptList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Wpt t) {
        Node node = null;
        try {
            node = new TrkPointNode(t);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }
}
