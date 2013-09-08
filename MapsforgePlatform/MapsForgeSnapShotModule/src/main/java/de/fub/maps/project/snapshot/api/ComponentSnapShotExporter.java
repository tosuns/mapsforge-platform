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
package de.fub.maps.project.snapshot.api;

import java.awt.Component;
import java.awt.Image;

/**
 * Interface to export a visual component of the application.
 *
 * @author Serdar
 */
public interface ComponentSnapShotExporter extends Comparable<ComponentSnapShotExporter> {

    /**
     * Returns in icon image that represents this exporter instance.
     *
     * @return A Image instance with a Dimension of 16x16.
     */
    public Image getIconImage();

    /**
     * Provides the name of this instance.
     *
     * @return A String instance, null not permitted.
     */
    public String getName();

    /**
     * Provides a short description of this instance.
     *
     * @return A String instance, null not permitted.
     */
    public String getShortDescription();

    /**
     * Export the specified Component.
     *
     * @param component a Component instance, null not permitted.
     */
    public void export(Component component);
}
