/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.agg2graph.roadgen.RoadNetwork;

/**
 *
 * @author Serdar
 */
public class OSMMapEvaluator {

    private final RoadNetwork roadNetwork;

    public OSMMapEvaluator(RoadNetwork roadNetwork) {
        assert roadNetwork != null;
        this.roadNetwork = roadNetwork;
    }

    public void evaluate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}