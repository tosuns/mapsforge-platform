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
        // Step 1: convert roadNetwork to osm
        // Step 2: fetch osm map with bounding box of roadnetwork
        // Strp 3: Build graphs for both osm files
        // Step 4: start mapMatcher
    }
}
