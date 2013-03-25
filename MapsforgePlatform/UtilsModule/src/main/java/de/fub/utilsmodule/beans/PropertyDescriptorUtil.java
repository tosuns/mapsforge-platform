/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.beans;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class PropertyDescriptorUtil {

    @SuppressWarnings({"unchecked"})
    public static <T> T getValue(Class<T> clazz, PropertyDescriptor property) {
        T instance = null;
        if (clazz.getName().equals(Boolean.class.getName())) {
            instance = (T) Boolean.valueOf(property.getValue());
        } else if (clazz.getName().equals(Double.class.getName())) {
            instance = (T) Double.valueOf(property.getValue());
        } else if (clazz.getName().equals(Integer.class.getName())) {
            instance = (T) Integer.valueOf(property.getValue());
        } else if (clazz.getName().equals(String.class.getName())) {
            instance = (T) property.getValue();
        } else if (clazz.getName().equals(Color.class.getName())) {
            instance = (T) new Color(Integer.parseInt(property.getValue(), 16));
        } else if (clazz.getName().equals(Long.class.getName())) {
            instance = (T) Long.valueOf(property.getValue());
        } else if (clazz.isEnum()) {
            T[] enumConstants = clazz.getEnumConstants();
            for (T enu : enumConstants) {
                if (enu.toString().equals(property.getValue())) {
                    instance = enu;
                    break;
                }
            }
        } else {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor(String.class);
                instance = constructor.newInstance(property.getValue());
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SecurityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return instance;
    }
}
