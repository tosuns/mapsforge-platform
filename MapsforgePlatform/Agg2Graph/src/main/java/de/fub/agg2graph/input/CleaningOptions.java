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
package de.fub.agg2graph.input;

import de.fub.agg2graph.agg.AggCleaner;

/**
 * Container class for several options to be used with {@link GPSCleaner} or
 * {@link AggCleaner}.
 *
 * @author Johannes Mitlmeier
 *
 */
public class CleaningOptions {
    // number of edges allowed per segment

    public boolean filterBySegmentLength = false;
    public long minSegmentLength = 1;
    public long maxSegmentLength = Long.MAX_VALUE;
    // min/max distance between two points (meters)
    public boolean filterByEdgeLength = true;
    public double minEdgeLength = 0.1;
    public double maxEdgeLength = 500;
    // length change between two consecutive edges
    public boolean filterByEdgeLengthIncrease = true;
    public double minEdgeLengthIncreaseFactor = 10;
    public double minEdgeLengthAfterIncrease = 30;
    // zigzag
    public boolean filterZigzag = true;
    public double maxZigzagAngle = 30;
    // fake circle
    public boolean filterFakeCircle = true;
    public double maxFakeCircleAngle = 50;
    // outliers
    public boolean filterOutliers = true;
    public int maxNumOutliers = 2;

    @Override
    public String toString() {
        return "CleaningOptions{" + "filterBySegmentLength=" + filterBySegmentLength + ", minSegmentLength=" + minSegmentLength + ", maxSegmentLength=" + maxSegmentLength + ", filterByEdgeLength=" + filterByEdgeLength + ", minEdgeLength=" + minEdgeLength + ", maxEdgeLength=" + maxEdgeLength + ", filterByEdgeLengthIncrease=" + filterByEdgeLengthIncrease + ", minEdgeLengthIncreaseFactor=" + minEdgeLengthIncreaseFactor + ", minEdgeLengthAfterIncrease=" + minEdgeLengthAfterIncrease + ", filterZigzag=" + filterZigzag + ", maxZigzagAngle=" + maxZigzagAngle + ", filterFakeCircle=" + filterFakeCircle + ", maxFakeCircleAngle=" + maxFakeCircleAngle + ", filterOutliers=" + filterOutliers + ", maxNumOutliers=" + maxNumOutliers + '}';
    }
}
