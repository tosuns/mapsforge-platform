/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.factories;

import de.fub.gpxmodule.xml.Wpt;
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
public class WptNodeFactory extends ChildFactory<Wpt> {

    private final List<Wpt> rteptList;

    public WptNodeFactory(List<Wpt> list) {
        assert list != null;
        this.rteptList = list;
    }

    @Override
    protected boolean createKeys(List<Wpt> list) {
        list.addAll(this.rteptList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Wpt t) {
        Node node = null;
        try {
            node = new BeanNode<Wpt>(t);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }
}
