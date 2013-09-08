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
package de.fub.maps.gpx.analysis.models.nodes;

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
