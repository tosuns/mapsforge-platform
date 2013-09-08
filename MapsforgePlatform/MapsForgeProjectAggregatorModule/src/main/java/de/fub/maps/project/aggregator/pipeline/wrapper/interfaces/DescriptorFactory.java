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
package de.fub.maps.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.maps.project.models.Aggregator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * A Generic Factory, which finds and instanciates all types which implements
 * the <code>Descriptor</code> interface.
 *
 * @author Serdar
 */
public class DescriptorFactory {

    /**
     * finds all instances of the provided <code>Class<T></code> argument.
     *
     * @param <T> extends <code>Descriptor</code>
     * @param clazz finds implemenations which extend this class.
     * @return A collection of instances, whose implement the Descriptor
     * interface.
     */
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

    /**
     * Creates an instance of <code>clazz</code> of the type specified with the
     * qualified name.
     *
     * @param <T>
     * @param clazz
     * @param qualifiedName
     * @return
     * @throws
     * de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.DescriptorFactory.InstanceNotFountException
     */
    public static <T extends Descriptor> T find(Class<T> clazz, String qualifiedName) throws InstanceNotFountException {
        return DescriptorFactory.find(clazz, qualifiedName, null);
    }

    /**
     * Creates an instance of <code>clazz</code> of type specified via the
     * qualified name. The provided Aggregator instance will be associated with
     * the created instance.
     *
     * @param <T>
     * @param clazz
     * @param qualifiedName
     * @param aggregator
     * @return
     * @throws
     * de.fub.maps.project.aggregator.pipeline.wrapper.interfaces.DescriptorFactory.InstanceNotFountException
     */
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
            throw new InstanceNotFountException(
                    MessageFormat.format("Couldn't find type {0}. Make sure type class was annotated with @ServiceProvider !", qualifiedName));
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
