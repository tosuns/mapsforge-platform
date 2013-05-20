/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.mapsforge.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public interface RoadNetworkDescriptor {

    public void setRoadNetworkProcess(RoadNetworkProcess roadNetworkProcess);

    public RoadNetworkProcess getRoadNetworkProcess();

    public PropertySet getProcessDescriptor();

    public Node getNodeDelegate();

    public static final class Factory {

        private static final Object MUTEX = new Object();

        public static <T extends RoadNetworkDescriptor> Collection<T> findAll(Class<T> instanceType) {
            synchronized (MUTEX) {
                List<T> list = new ArrayList<T>();
                Set<Class<? extends T>> allClasses = Lookup.getDefault().lookupResult(instanceType).allClasses();
                for (Class<? extends T> clazz : allClasses) {
                    try {
                        T filter = clazz.newInstance();
                        list.add(filter);
                    } catch (Throwable ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                return list;
            }
        }

        public static <T extends RoadNetworkDescriptor> T find(String qualifiedName, Class<T> clazz) throws InstanceNotFound {
            synchronized (MUTEX) {
                T filter = null;
                Collection<T> instances = findAll(clazz);
                for (T instance : instances) {
                    if (instance.getClass().getName().equals(qualifiedName)) {
                        filter = instance;
                        break;
                    }
                }
                if (filter == null) {
                    throw new InstanceNotFound("Couldn'T find " + qualifiedName + "! Make sure the class is annotated with '@ServiceProvider' and implements RoadNetworkDescriptor.");
                }

                return filter;
            }
        }
    }

    public static class InstanceNotFound extends Exception {

        private static final long serialVersionUID = 1L;

        public InstanceNotFound() {
        }

        public InstanceNotFound(String message) {
            super(message);
        }

        public InstanceNotFound(String message, Throwable cause) {
            super(message, cause);
        }

        public InstanceNotFound(Throwable cause) {
            super(cause);
        }
    }
}
