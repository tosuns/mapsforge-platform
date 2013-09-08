/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.model.gpx;

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
