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
package de.fub.maps.project.detector.model;

import de.fub.maps.project.detector.model.gpx.TrackSegment;
import java.util.List;
import java.util.Map;

/**
 * Interface to provide access to a trainings set..
 *
 * @author Serdar
 */
public interface TrainingsDataProvider {

    /**
     * Methode to provide the name of this provider. Only use to provider Meta
     * data.
     *
     * @return String, the name of this provider.
     */
    public String getName();

    /**
     * Returns the trainings data as a map.
     *
     * @return a Map with training data set.
     */
    public Map<String, List<TrackSegment>> getData();
}
