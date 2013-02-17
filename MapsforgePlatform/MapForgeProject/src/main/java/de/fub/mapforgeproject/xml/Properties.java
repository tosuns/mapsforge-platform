/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Properties implements Collection<Property> {

    public static final String PROP_NAME_PROPERT_LIST = "properties.list";
    @XmlElement(name = "property")
    private List<Property> properties = new ArrayList<Property>();
    protected final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Properties() {
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public boolean contains(Property o) {
        return properties.contains(o);
    }

    @Override
    public Property[] toArray() {
        return properties.toArray(new Property[size()]);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return properties.toArray();
    }

    @Override
    public boolean add(Property e) {
        boolean result = properties.add(e);
        if (result) {
            firePropertListChangeEvent();
        }
        return result;
    }

    public boolean remove(Property o) {
        boolean result = properties.remove(o);
        if (result) {
            firePropertListChangeEvent();
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends Property> c) {
        boolean result = properties.addAll(c);
        if (result) {
            firePropertListChangeEvent();
        }
        return result;
    }

    public boolean addAll(int index, Collection<? extends Property> c) {
        boolean result = properties.addAll(index, c);
        if (result) {
            firePropertListChangeEvent();
        }
        return result;
    }

    @Override
    public void clear() {
        properties.clear();
        firePropertListChangeEvent();
    }

    public Property get(int index) {
        return properties.get(index);
    }

    public void add(int index, Property element) {
        properties.add(index, element);
        firePropertListChangeEvent();
    }

    public Property remove(int index) {
        Property prop = properties.remove(index);
        if (prop != null) {
            firePropertListChangeEvent();
        }
        return prop;
    }

    public int indexOf(Property o) {
        return properties.indexOf(o);
    }

    private void firePropertListChangeEvent() {
        pcs.firePropertyChange(PROP_NAME_PROPERT_LIST, properties, properties);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Properties{" + "property=" + properties + '}';
    }

    @Override
    public boolean contains(Object o) {
        return properties.contains(o);
    }

    @Override
    public Iterator<Property> iterator() {
        return properties.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return properties.remove(o);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return properties.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = properties.removeAll(c);
        if (result) {
            firePropertListChangeEvent();
        }
        return result;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return properties.containsAll(c);
    }
}
