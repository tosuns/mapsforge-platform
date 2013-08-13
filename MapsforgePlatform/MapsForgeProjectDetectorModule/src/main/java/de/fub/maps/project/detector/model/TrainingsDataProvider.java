/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model;

import de.fub.maps.project.detector.model.gpx.TrackSegment;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Serdar
 */
public interface TrainingsDataProvider {

    public String getName();

    public Map<String, List<TrackSegment>> getData();
}
