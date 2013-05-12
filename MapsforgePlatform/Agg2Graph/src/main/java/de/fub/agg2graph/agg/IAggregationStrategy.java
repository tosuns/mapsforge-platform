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
package de.fub.agg2graph.agg;

import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSSegment;
import java.util.List;

/**
 * Strategy for aggregating new {@link GPSSegment}s to an {@link AggContainer}.
 *
 * @author Johannes Mitlmeier
 */
public interface IAggregationStrategy {

    public void aggregate(GPSSegment segment);

    public void setAggContainer(AggContainer aggContainer);

    public AggContainer getAggContainer();

    public AggConnection mergeConnections(AggConnection changedConn,
            AggConnection oldConn);

    public AggConnection combineConnections(AggConnection firstConn,
            AggConnection secondConn);

    public void clear();

    public ITraceDistance getTraceDist();

    public List<ClassObjectEditor> getSettings();
}
