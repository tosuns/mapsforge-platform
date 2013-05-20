/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.mapsforge.project.models.Aggregator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class DescriptorFactory {

    public static <T extends Descriptor> Collection<? extends T> findAll(Class<T> clazz) {
        Collection<? extends T> allInstances = Lookup.getDefault().lookupResult(clazz).allInstances();
        List<T> list = new ArrayList<T>(allInstances.size());

        for (T instanceClazz : allInstances) {
            try {
                @SuppressWarnings("unchecked")
                T instance = (T) instanceClazz.getClass().newInstance();
                list.add(instance);
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return list;
    }

    public static <T extends Descriptor> T find(Class<T> clazz, String qualifiedName) throws InstanceNotFountException {
        return DescriptorFactory.find(clazz, qualifiedName, null);
    }

    public static <T extends Descriptor> T find(Class<T> clazz, String qualifiedName, Aggregator aggregator) throws InstanceNotFountException {
        Collection<? extends T> list = findAll(clazz);
        T instance = null;
        for (T classInstance : list) {
            if (classInstance != null
                    && classInstance.getClass().getName().equals(qualifiedName)) {
                instance = classInstance;
                instance.setAggregator(aggregator);
            }
        }
        if (instance == null) {
            throw new InstanceNotFountException(MessageFormat.format("Couldn't find type {0}. Make sure type class was annotated with @ServiceProvider !", qualifiedName));
        }
        return instance;
    }

    public static class InstanceNotFountException extends Exception {

        private static final long serialVersionUID = 1L;

        public InstanceNotFountException() {
        }

        public InstanceNotFountException(String message) {
            super(message);
        }

        public InstanceNotFountException(String message, Throwable cause) {
            super(message, cause);
        }

        public InstanceNotFountException(Throwable cause) {
            super(cause);
        }
    }
}
