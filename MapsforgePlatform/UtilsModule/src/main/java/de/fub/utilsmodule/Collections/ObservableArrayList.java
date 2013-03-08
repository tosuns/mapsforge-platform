/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.Collections;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
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
