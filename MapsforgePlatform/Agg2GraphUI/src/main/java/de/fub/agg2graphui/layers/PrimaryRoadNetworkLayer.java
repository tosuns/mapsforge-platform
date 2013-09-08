/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.roadgen.Road;
import java.awt.Color;

/**
 *
 * @author Serdar
 */
public class PrimaryRoadNetworkLayer extends RoadNetworkLayer {

    public PrimaryRoadNetworkLayer() {
        super("Primary Roads", "Displays primary roads of the road network.");
        setRoadType(Road.RoadType.PRIMARY);
        getRenderingOptions().setColor(new Color(219, 37, 37));
    }
}
