/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.openstreetmap.ui.controller;

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
