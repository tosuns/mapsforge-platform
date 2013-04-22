/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.gpx.analysis.models.nodes;

import java.awt.Image;
import java.beans.IntrospectionException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public abstract class CustomNode extends AbstractNode {

    private static final Node EMPTY_NODE = createEmptyNode();

    public CustomNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public CustomNode(Children children) {
        super(children);
    }

    @Override
    public Image getIcon(int type) {
        return EMPTY_NODE.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    private static Node createEmptyNode() {
        Node node = null;
        try {
            node = new BeanNode<Object>(new Object());
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node == null ? Node.EMPTY : node;
    }
}
