/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.factories;

import de.fub.gpxmodule.xml.gpx.RteType.Rtept;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class RteptNodeFactory extends ChildFactory<Rtept> {

    private final List<Rtept> rteptList;

    public RteptNodeFactory(List<Rtept> list) {
        assert list != null;
        this.rteptList = list;
    }

    @Override
    protected boolean createKeys(List<Rtept> list) {
        list.addAll(this.rteptList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Rtept t) {
        Node node = null;
        try {
            node = new BeanNode<Rtept>(t);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }
}
