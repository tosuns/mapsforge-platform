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
package de.fub.utilsmodule.NodeFactories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author Serdar
 * @param <T>
 */
public abstract class MutableChildFactory<T> extends ChildFactory<T> {

    private final List<T> items;

    public MutableChildFactory() {
        this(new ArrayList<T>());
    }

    public MutableChildFactory(List<T> items) {
        super();
        this.items = items;
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean add(T e) {
        boolean result = items.add(e);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public boolean remove(T o) {
        boolean result = items.remove(o);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public boolean addAll(Collection<? extends T> c) {
        boolean result = items.addAll(c);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public boolean removeAll(Collection<?> c) {
        boolean result = items.removeAll(c);
        if (result) {
            refresh(true);
        }
        return result;
    }

    public void clear() {
        items.clear();
        refresh(true);
    }
}
