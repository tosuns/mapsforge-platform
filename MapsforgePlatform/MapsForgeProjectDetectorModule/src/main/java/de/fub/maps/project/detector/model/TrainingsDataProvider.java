/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
