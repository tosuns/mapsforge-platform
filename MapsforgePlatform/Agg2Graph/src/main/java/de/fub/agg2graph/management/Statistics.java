/**
 * *****************************************************************************
 * Copyright 2013 Johannes Mitlmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
package de.fub.agg2graph.management;

import de.fub.agg2graph.roadgen.RoadNetwork;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Statistics {

    private static final Logger LOG = Logger.getLogger(Statistics.class.getName());
    public static final String PROP_NAME_TOTAL_NUMBER_OF_ROADS = "total number of roads";
    public static final String PROP_NAME_TOTAL_NUMBER_OF_INTERSECTIONS = "total number of intersections";
    public static final String PROP_NAME_ROAD_INTERSECTION_RATIO = "roads/intersections";
    public static final String PROP_NAME_TOTAL_ROAD_LENGTH = "total road length";
    public static final String PROP_NAME_AVERAGE_ROAD_LENGTH = "average road length";
    public static final String PROP_NAME_NUMBER_OF_REAL_INTERSECTIONS = "number of real intersections";
    public static final String PROP_NAME_NUMBER_OF_PSEUDO_INTERSECTIONS = "number of pseudo intersections";
    public static final String PROP_NAME_REAL_TO_PSEUDO_INTERSECTION_RATIO = "real/pseudo intersections";
    public static final String PROP_NAME_NUMBER_OF_ISOLATED_ROADS = "number of isolated roads";
    public static final String PROP_NAME_NUMBER_OF_ONE_WAY_ROADS = "number of one way roads";
    public static final String PROP_NAME_NUMBER_OF_TWO_WAY_ROADS = "number of two way roads";
    public static final String PROP_NAME_ONE_WAY_TWO_WAY_ROAD_RATIO = "one way/two way roads";

    /**
     * Get statistical information about the {@link RoadNetwork} object given.
     *
     * @return
     */
    public static Map<String, Double> getData(RoadNetwork roadNetwork) {
        Map<String, Double> stats = new HashMap<String, Double>();
        stats.put(PROP_NAME_TOTAL_NUMBER_OF_ROADS, roadNetwork.getRoadCount());
        stats.put(PROP_NAME_TOTAL_NUMBER_OF_INTERSECTIONS, roadNetwork.getIntersectionCount());
        stats.put(PROP_NAME_ROAD_INTERSECTION_RATIO, roadNetwork.getRoadIntersectionRatio());

        // compute average road length
        stats.put(PROP_NAME_TOTAL_ROAD_LENGTH, roadNetwork.getTotalRoadLength());
        stats.put(PROP_NAME_AVERAGE_ROAD_LENGTH, roadNetwork.getAverageRoadLength());

        // how many intersections are only pseudo intersections?
        stats.put(PROP_NAME_NUMBER_OF_REAL_INTERSECTIONS, roadNetwork.getRealIntersectionCount());
        stats.put(PROP_NAME_NUMBER_OF_PSEUDO_INTERSECTIONS, roadNetwork.getPseudoIntersectionCount());
        stats.put(PROP_NAME_REAL_TO_PSEUDO_INTERSECTION_RATIO, roadNetwork.getRealToPseudoIntersectionRatio());

        // find isolated roads
        stats.put(PROP_NAME_NUMBER_OF_ISOLATED_ROADS, roadNetwork.getIsolatedRoadCount());

        // count one way roads
        stats.put(PROP_NAME_NUMBER_OF_ONE_WAY_ROADS, roadNetwork.getOneWayRoadCount());
        stats.put(PROP_NAME_NUMBER_OF_TWO_WAY_ROADS, roadNetwork.getTwoWayRoadCount());
        stats.put(PROP_NAME_ONE_WAY_TWO_WAY_ROAD_RATIO, roadNetwork.getOneWayTwoWayRoadRatio());

        return stats;
    }
}
