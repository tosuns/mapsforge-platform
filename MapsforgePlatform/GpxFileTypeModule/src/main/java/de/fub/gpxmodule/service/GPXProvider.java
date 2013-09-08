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
package de.fub.gpxmodule.service;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer2;
import javax.swing.event.ChangeListener;

/**
 * Interface for GPX providers to access the gpx model.
 *
 * @author Serdar
 */
public interface GPXProvider extends ModelSynchronizer2<Gpx> {

    /**
     * Return an gpx instance.
     *
     * @return Gpx instance if the underling xml file could successfully be
     * parsed, otherwise null.
     */
    public Gpx getGpx();

    /**
     * Adds a listener to get notified if the underlying xml file gets modified
     *
     * @param listener
     */
    @Override
    public void addChangeListener(ChangeListener listener);

    /**
     * remove a listener from this provider.
     *
     * @param listener
     */
    @Override
    public void removeChangeListener(ChangeListener listener);
}
