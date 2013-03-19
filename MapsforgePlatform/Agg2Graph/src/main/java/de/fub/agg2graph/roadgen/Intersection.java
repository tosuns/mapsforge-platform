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
package de.fub.agg2graph.roadgen;

import de.fub.agg2graph.agg.AggNode;
import java.util.HashSet;
import java.util.Set;

/**
 * An intersection in the {@link RoadNetwork} which can be either a real
 * intersection or a pseudo intersection (dead-end road).
 *
 * @author Johannes Mitlmeier
 *
 */
public class Intersection extends RoadNode {

    public Set<Road> out = new HashSet<Road>();
    public Set<Road> in = new HashSet<Road>();
    public AggNode baseNode;
    public Intersection mergedTo = null;

    public Intersection(AggNode baseNode) {
        super(baseNode);
        this.baseNode = baseNode;
        if (baseNode != null) {
            baseNode.setIntersection(this);
        }
    }

    @Override
    public String toString() {
        if (getID() != null) {
            return String.format("intersection %s\n", getID());
        } else {
            return String.format("intersection at [%.7f, %.7f]\n", getLat(),
                    getLon());
        }
    }

    @Override
    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        if (getID() != null) {
            sb.append(String.format("intersection %s\n", getID()));
        } else {
            sb.append(String.format("intersection at [%.7f, %.7f]\n", getLat(),
                    getLon()));
        }
        for (Road r : out) {
            sb.append("\t")
                    .append(String.format("road to %s (%d points)", r.getTo()
                    .getID(), r.path.size())).append("\n");
        }
        for (Road r : in) {
            sb.append("\t")
                    .append(String.format("road from %s (%d points)", r.getTo()
                    .getID(), r.path.size())).append("\n");
        }
        return sb.toString();
    }

    public boolean isPseudo() {
        if (in.size() == 0 || out.size() == 0) {
            return true;
        }
        return false;
    }

    public int getVisibleEdgeCount() {
        int sum = 0;
        for (Road r : in) {
            if (r.isVisible()) {
                sum++;
            }
        }
        for (Road r : out) {
            if (r.isVisible()) {
                sum++;
            }
        }
        return sum;
    }

    public boolean isDirectlyConnectedTo(Intersection i2) {
        for (Road r : in) {
            if (r.isVisible() && r.getFrom().equals(i2)) {
                return true;
            }
        }
        for (Road r : out) {
            if (r.isVisible() && r.getTo().equals(i2)) {
                return true;
            }
        }
        return false;
    }
}
