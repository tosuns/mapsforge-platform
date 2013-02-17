/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.NodeFactories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author Serdar
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
