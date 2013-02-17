/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.factories;

import de.fub.gpxmodule.nodes.TrkPointNode;
import de.fub.gpxmodule.xml.Trkseg.Trkpt;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class TrkpointNodeFactory extends ChildFactory<Trkpt> {

    private final List<Trkpt> trkptList;

    public TrkpointNodeFactory(List<Trkpt> list) {
        assert list != null;
        this.trkptList = list;
    }

    @Override
    protected boolean createKeys(List<Trkpt> list) {
        list.addAll(this.trkptList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Trkpt t) {
        Node node = null;
        try {
            node = new TrkPointNode(t);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }
}
