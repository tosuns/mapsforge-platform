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
