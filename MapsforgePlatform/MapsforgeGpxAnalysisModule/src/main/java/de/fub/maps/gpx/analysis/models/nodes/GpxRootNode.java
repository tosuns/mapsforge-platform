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
package de.fub.maps.gpx.analysis.models.nodes;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.maps.gpx.analysis.models.factories.TrackNodeFactory;
import java.util.concurrent.Callable;
import org.openide.nodes.Children;

/**
 *
 * @author Serdar
 */
public class GpxRootNode extends CustomNode {

    public GpxRootNode(final Gpx gpx) {
        super(Children.createLazy(new Callable<Children>() {
            @Override
            public Children call() throws Exception {
                return gpx.getTrk().isEmpty() ? Children.LEAF : Children.create(new TrackNodeFactory(gpx.getTrk()), true);
            }
        }));
    }
}
