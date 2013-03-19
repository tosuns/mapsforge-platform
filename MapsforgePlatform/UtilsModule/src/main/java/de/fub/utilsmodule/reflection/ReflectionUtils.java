/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class ReflectionUtils {

    public static Object createInstance(String fullyQualifiedName, String serializedValue) throws ClassNotFoundException {
        return createInstance(Class.forName(fullyQualifiedName), serializedValue);
    }

    public static Object createInstance(Class<?> clazz, String serializedValue) {
        Object resultInstance = null;
        try {
            if (!clazz.isEnum()) {
                Constructor<?> constructor = clazz.getConstructor(serializedValue.getClass());
                resultInstance = constructor.newInstance(serializedValue);
            } else {
                // TODO handle enum generation.
            }
        } catch (NoSuchMethodException ex) {
            Collection<? extends TypeConverter> allInstances = Lookup.getDefault().lookupResult(TypeConverter.class).allInstances();

            // TODO  the destination type doesn't have a constuctor with a string parameter
            // in this cass as fall back we have to look up for converter classes,
            // that can handle the source type and destination type.
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
        return resultInstance;
    }

    public interface TypeConverter<I, O> {

        public O convert(I input);

        public I convertBack(O input);
    }
}
