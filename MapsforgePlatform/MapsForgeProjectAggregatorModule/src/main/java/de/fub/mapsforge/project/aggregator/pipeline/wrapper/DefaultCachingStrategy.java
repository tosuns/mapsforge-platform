/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper;

import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = ICachingStrategy.class)
public class DefaultCachingStrategy extends de.fub.agg2graph.agg.tiling.DefaultCachingStrategy {
}
