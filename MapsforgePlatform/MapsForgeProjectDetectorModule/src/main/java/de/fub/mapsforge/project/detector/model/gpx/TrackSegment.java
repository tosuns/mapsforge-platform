/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.gpx;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Serdar
 */
public class TrackSegment {

    private final ObservableArrayList<Waypoint> wayPointList = new ObservableArrayList<Waypoint>();
    private String label;

    public TrackSegment() {
    }

    public TrackSegment(String label) {
        this.label = label;
    }

    public List<Waypoint> getWayPointList() {
        return wayPointList;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean add(Waypoint e) {
        return wayPointList.add(e);
    }

    public void add(int index, Waypoint element) {
        wayPointList.add(index, element);
    }

    public Waypoint remove(int index) {
        return wayPointList.remove(index);
    }

    public boolean remove(Waypoint o) {
        return wayPointList.remove(o);
    }

    public void clear() {
        wayPointList.clear();
    }

    public boolean addAll(Collection<? extends Waypoint> c) {
        return wayPointList.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends Waypoint> c) {
        return wayPointList.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return wayPointList.removeAll(c);
    }

    public void addChangeListener(ChangeListener listener) {
        wayPointList.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        wayPointList.removeChangeListener(listener);
    }
}
