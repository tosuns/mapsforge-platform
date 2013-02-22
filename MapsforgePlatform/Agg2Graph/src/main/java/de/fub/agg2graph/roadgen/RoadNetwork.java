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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.ui.StepStorage;
import de.fub.agg2graph.ui.gui.UIStepStorage;

/**
 * Data structure for the street graph representation.
 *
 * @author Johannes Mitlmeier
 */
public class RoadNetwork {

    public IAggFilter aggFilter = AggFilterFactory.getObject();
    public IIntersectionParser intersectionParser = IntersectionParserFactory
            .getObject();
    public Set<Intersection> intersections;
    public Set<Road> roads;
    public IRoadTypeClassifier roadTypeClassifier = RoadTypeClassifierFactory
            .getObject();
    public IRoadNetworkFilter roadNetworkFilter = RoadNetworkFilterFactory
            .getObject();
    public IRoadObjectMerger roadObjectMerger = RoadObjectMergerFactory
            .getObject();

    public RoadNetwork() {
        clear();
    }

    public void parse(AggContainer agg, StepStorage stepStorage) {
        // pre-filtering
        aggFilter.filter(agg);
        intersectionParser.makeNetwork(this, agg);
        roadTypeClassifier.classify(this);
        roadObjectMerger.mergeInteresections(this);
        roadObjectMerger.mergeRoads(this);
        roadNetworkFilter.filter(this);

        if (stepStorage != null) {
            if (UIStepStorage.class.isAssignableFrom(stepStorage.getClass())) {
                UIStepStorage uiStepStorage = (UIStepStorage) stepStorage;
                uiStepStorage.intersectionLayer.clear();
                uiStepStorage.intersectionLayer.addObject(this.intersections);
                uiStepStorage.roadLayer.clear();
                uiStepStorage.roadLayer.addObject(this);
            }
        }
    }

    public void clear() {
        intersections = new HashSet<Intersection>(100);
        roads = new HashSet<Road>(100);
    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder("RoadNetwork:\n");
        for (Intersection i : intersections) {
            sb.append("\t").append(i.toDebugString().replace("\n", "\n\t"))
                    .append("\n");
        }
        return sb.toString();
    }

    public List<ClassObjectEditor> getSettings() {
        List<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>(3);
        result.add(new ClassObjectEditor(aggFilter));
        result.add(new ClassObjectEditor(this, Arrays.asList(new String[]{
            "aggFilter", "intersectionParser", "roadNetworkFilter",
            "intersections", "roads", "roadTypeClassifier",
            "roadObjectMerger"})));
        result.add(new ClassObjectEditor(roadObjectMerger));
        result.add(new ClassObjectEditor(roadTypeClassifier));
        result.add(new ClassObjectEditor(roadNetworkFilter));
        return result;
    }
}
