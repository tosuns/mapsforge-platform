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
*****************************************************************************
 */
package de.fub.agg2graph.roadgen;

/**
 * Filtering methods for the {@link RoadNetwork}. Used to strip unreliable
 * roads.
 *
 * @author Johannes Mitlmeier
 *
 */
public class DefaultRoadNetworkFilter implements IRoadNetworkFilter {

    public boolean removeBorderRoads = true;
    public double minBorderRoadLength = 150; // meters
    public boolean removeIsolatedRoads = true;
    public double minIsolatedRoadLength = 500; // meters

    @Override
    public void filter(RoadNetwork roadNetwork) {
        hideUnreliableRoads(roadNetwork);
    }

    private void hideUnreliableRoads(RoadNetwork roadNetwork) {
        for (Road r : roadNetwork.roads) {
            r.setVisible(true);
            if (!removeBorderRoads && !removeIsolatedRoads) {
                continue;
            }
            double length = r.getLength();

            // remove isolated roads below length limit if requested
            if (removeBorderRoads && r.isBorderRoad()
                    && length < minBorderRoadLength) {
                hideRoad(r);
            }

            // remove isolated roads below length limit if requested
            if (removeIsolatedRoads && r.isIsolated()
                    && length < minIsolatedRoadLength) {
                hideRoad(r);
            }
        }
    }

    private void hideRoad(Road r) {
        r.setVisible(false);
        // check if we have to hide intersections as well
        if (r.getFrom().getVisibleEdgeCount() == 0) {
            r.getFrom().setVisible(false);
        }
        if (r.getTo().getVisibleEdgeCount() == 0) {
            r.getTo().setVisible(false);
        }
    }
}
