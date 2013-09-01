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
package de.fub.maps.project.plugins.tasks.eval;

import de.fub.agg2graphui.controller.AbstractLayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class LayerNodeFactory extends ChildFactory<AbstractLayer<?>> {

    private final ArrayList<AbstractLayer<?>> layers = new ArrayList<AbstractLayer<?>>();

    public LayerNodeFactory() {
    }

    @Override
    protected boolean createKeys(List<AbstractLayer<?>> toPopulate) {
        toPopulate.addAll(layers);
        return true;
    }

    @Override
    protected Node createNodeForKey(AbstractLayer<?> layer) {
        return new FilterNode(layer.getNodeDelegate());
    }

    public boolean add(AbstractLayer<?> layer) {
        boolean result = layers.add(layer);
        refresh(true);
        return result;
    }

    public boolean remove(AbstractLayer<?> layer) {
        boolean result = layers.remove(layer);
        refresh(true);
        return result;
    }

    public void clear() {
        layers.clear();
        refresh(true);
    }

    public boolean addAll(Collection<? extends AbstractLayer<?>> layer) {
        boolean result = layers.addAll(layer);
        refresh(true);
        return result;
    }

    public boolean removeAll(Collection<AbstractLayer<?>> layer) {
        boolean result = layers.removeAll(layer);
        refresh(true);
        return result;
    }
}
