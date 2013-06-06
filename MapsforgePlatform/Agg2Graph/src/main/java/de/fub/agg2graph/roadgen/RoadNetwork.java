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

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.ui.StepStorage;
import de.fub.agg2graph.ui.gui.UIStepStorage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Data structure for the street graph representation.
 *
 * @author Johannes Mitlmeier
 */
public class RoadNetwork {

    private static final Logger LOG = Logger.getLogger(RoadNetwork.class.getName());
    private IAggFilter aggFilter = AggFilterFactory.getObject();
    private IIntersectionParser intersectionParser = IntersectionParserFactory.getObject();
    private Set<Intersection> intersections;
    private Set<Road> roads;
    private IRoadTypeClassifier roadTypeClassifier = RoadTypeClassifierFactory.getObject();
    private IRoadNetworkFilter roadNetworkFilter = RoadNetworkFilterFactory.getObject();
    private IRoadObjectMerger roadObjectMerger = RoadObjectMergerFactory.getObject();
    private double totalRoadLength;
    private int isolatedSum;
    private int oneWaySum;
    private int visibleRoadCount;
    private int visibleIntersectionCount;
    private int pseudoSum;

    public RoadNetwork() {
        init();
    }

    private void init() {
        clear();
    }

    public IAggFilter getAggFilter() {
        return aggFilter;
    }

    public void setAggFilter(IAggFilter aggFilter) {
        this.aggFilter = aggFilter;
    }

    public IIntersectionParser getIntersectionParser() {
        return intersectionParser;
    }

    public void setIntersectionParser(IIntersectionParser intersectionParser) {
        this.intersectionParser = intersectionParser;
    }

    public Set<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(Set<Intersection> intersections) {
        this.intersections = intersections;
    }

    public Set<Road> getRoads() {
        return roads;
    }

    public void setRoads(Set<Road> roads) {
        this.roads = roads;
    }

    public IRoadTypeClassifier getRoadTypeClassifier() {
        return roadTypeClassifier;
    }

    public void setRoadTypeClassifier(IRoadTypeClassifier roadTypeClassifier) {
        this.roadTypeClassifier = roadTypeClassifier;
    }

    public IRoadNetworkFilter getRoadNetworkFilter() {
        return roadNetworkFilter;
    }

    public void setRoadNetworkFilter(IRoadNetworkFilter roadNetworkFilter) {
        this.roadNetworkFilter = roadNetworkFilter;
    }

    public IRoadObjectMerger getRoadObjectMerger() {
        return roadObjectMerger;
    }

    public void setRoadObjectMerger(IRoadObjectMerger roadObjectMerger) {
        this.roadObjectMerger = roadObjectMerger;
    }

    public double getTotalRoadLength() {
        return totalRoadLength;
    }

    public double getAverageRoadLength() {
        return getRoadCount() == 0 ? 0 : getTotalRoadLength() / getRoadCount();
    }

    public double getRoadCount() {
        return visibleRoadCount;
    }

    public double getIsolatedRoadCount() {
        return isolatedSum;
    }

    public double getOneWayRoadCount() {
        return oneWaySum;
    }

    public double getTwoWayRoadCount() {
        return getRoadCount() - getOneWayRoadCount();
    }

    public double getOneWayTwoWayRoadRatio() {
        return getTwoWayRoadCount() == 0 ? 0 : getOneWayRoadCount() / getTwoWayRoadCount();
    }

    public double getPseudoIntersectionCount() {
        return pseudoSum;
    }

    public double getIntersectionCount() {
        return visibleIntersectionCount;
    }

    public double getRealIntersectionCount() {
        return getIntersectionCount() - getPseudoIntersectionCount();
    }

    public double getRoadIntersectionRatio() {
        return getIntersectionCount() == 0 ? 0 : getRoadCount() / getIntersectionCount();
    }

    public double getRealToPseudoIntersectionRatio() {
        return getPseudoIntersectionCount() == 0 ? 0 : getIntersectionCount() / getPseudoIntersectionCount();
    }

    public void parse(AggContainer agg, StepStorage stepStorage) {
        // pre-filtering
        aggFilter.filter(agg);
        intersectionParser.makeNetwork(this, agg);
        roadTypeClassifier.classify(this);
        roadObjectMerger.mergeInteresections(this);
        roadObjectMerger.mergeRoads(this);
        roadNetworkFilter.filter(this);
        computeStatistics();

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

    private void computeStatistics() {
        totalRoadLength = 0d;
        pseudoSum = 0;
        isolatedSum = 0;
        oneWaySum = 0;
        visibleRoadCount = 0;
        visibleIntersectionCount = 0;
        for (Road road : getRoads()) {
            if (road.isVisible()) {
                visibleRoadCount++;
                totalRoadLength += road.getLength();
                if (road.isIsolated()) {
                    isolatedSum++;
                }
                if (road.isOneWay()) {
                    oneWaySum++;
                }
            } else {
                LOG.fine(MessageFormat.format("invisible road {0}", road));
            }
        }
        for (Intersection i : getIntersections()) {
            if (i.isVisible()) {
                visibleIntersectionCount++;
                if (i.isPseudo()) {
                    pseudoSum++;
                }
            } else {
                LOG.fine(MessageFormat.format("invisible intersection {0}", i));
            }
        }
    }
}
