/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper.interfaces;

import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregatorUtils;
import java.util.Collection;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
class DescriptorFactory {

    public static <T extends Descriptor> Collection<? extends T> findAll(Class<T> clazz) {
        return Lookup.getDefault().lookupResult(clazz).allInstances();
    }

    public static <T extends Descriptor> T find(Class<T> clazz, String qualifiedName) {
        return DescriptorFactory.find(clazz, qualifiedName, null);
    }

    public static <T extends Descriptor> T find(Class<T> clazz, String qualifiedName, Aggregator aggregator) {
        T instance = AggregatorUtils.createInstance(clazz, qualifiedName);
        if (instance != null) {
            instance.setAggregator(aggregator);
        }
        return instance;
    }
}
