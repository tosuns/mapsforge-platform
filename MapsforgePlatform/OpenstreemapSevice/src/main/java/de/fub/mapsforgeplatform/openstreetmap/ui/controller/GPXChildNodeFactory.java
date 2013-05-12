/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class GPXChildNodeFactory extends ChildFactory<Node> {

    private final ArrayList<Node> nodes = new ArrayList<Node>();

    public GPXChildNodeFactory() {
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.addAll(nodes);
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return node;
    }

    public int size() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int lastIndexOf(Object o) {
        return nodes.lastIndexOf(o);
    }

    public boolean add(Node e) {
        boolean result = nodes.add(e);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public void add(int index, Node element) {
        nodes.add(index, element);
        refresh(true);
    }

    public Node remove(int index) {
        Node result = nodes.remove(index);
        if (result != null) {
            refresh(true);
        }
        return result;
    }

    public boolean remove(Node o) {
        boolean result = nodes.remove(o);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public void clear() {
        nodes.clear();
        refresh(true);
    }

    public boolean addAll(Collection<? extends Node> c) {
        boolean result = nodes.addAll(c);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public boolean addAll(int index, Collection<? extends Node> c) {
        boolean result = nodes.addAll(index, c);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public boolean removeAll(Collection<?> c) {
        boolean result = nodes.removeAll(c);
        if (result) {
            refresh(true);
        }
        return result;
    }
}
