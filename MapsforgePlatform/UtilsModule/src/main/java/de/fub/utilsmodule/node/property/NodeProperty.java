/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.node.property;

import de.fub.utilsmodule.beans.PropertyDescriptor;
import de.fub.utilsmodule.beans.PropertyDescriptorUtil;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class NodeProperty extends PropertySupport.ReadWrite<Object> {

    private final PropertyDescriptor property;
    private Object value = null;

    @SuppressWarnings("unchecked")
    public NodeProperty(PropertyDescriptor property) {
        super(property.getName(), getClassOf(property), property.getName(), property.getDescription());
        this.property = property;
        initValue();
    }

    private void initValue() {
        try {
            value = PropertyDescriptorUtil.getValue(Class.forName(property.getJavaType()), property);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return value;
    }

    @Override
    public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        value = val;
        if (val != null && !val.equals(property.getValue())) {
            property.setValue(String.valueOf(val));
        } else {
            property.setValue(null);
        }
    }

    private static Class getClassOf(PropertyDescriptor property) {
        Class<?> clazz = Object.class;
        try {
            if (property != null && property.getJavaType() != null) {
                clazz = Class.forName(property.getJavaType());
            } else {
                clazz = Object.class;
            }
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return clazz;
    }
}