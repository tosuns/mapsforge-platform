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
package de.fub.maps.project.datasource.spi;

import de.fub.gpxmodule.xml.Trkseg;
import java.awt.Color;

/**
 *
 * @author Serdar
 */
public class TrksegWrapper {

    private final Color color;
    private final Trkseg trkseg;
    private final String trackDescription;
    private final String trackName;

    public TrksegWrapper(String name, String desc, Trkseg trkseg, Color color) {
        this.color = color;
        this.trkseg = trkseg;
        this.trackName = name;
        this.trackDescription = desc;
    }

    public Color getColor() {
        return color;
    }

    public Trkseg getTrkseg() {
        return trkseg;
    }

    public String getTrackDescription() {
        return trackDescription;
    }

    public String getTrackName() {
        return trackName;
    }
}
