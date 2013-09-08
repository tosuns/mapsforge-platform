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
package de.fub.utilsmodule.Collections;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
 * @param <T>
 */
public class ObservableArrayList<T> extends ArrayList<T> implements ObservableList<T> {

    private static final long serialVersionUID = 1L;
    private transient final ChangeSupport csp = new ChangeSupport(this);

    public ObservableArrayList() {
    }

    public ObservableArrayList(Collection<? extends T> c) {
        super(c);
    }

    @Override
    public boolean add(T e) {
        boolean result = super.add(e);
        if (result) {
            csp.fireChange();
        }
        return result;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        csp.fireChange();
    }

    @Override
    public T remove(int index) {
        T element = super.remove(index);
        if (element != null) {
            csp.fireChange();
        }
        return element;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if (result) {
            csp.fireChange();
        }
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        csp.fireChange();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = super.addAll(c);
        if (result) {
            csp.fireChange();
        }
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean result = super.addAll(index, c);
        if (result) {
            csp.fireChange();
        }
        return result;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        csp.fireChange();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = super.removeAll(c);
        if (result) {
            csp.fireChange();
        }
        return result;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        csp.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        csp.removeChangeListener(listener);
    }
}
