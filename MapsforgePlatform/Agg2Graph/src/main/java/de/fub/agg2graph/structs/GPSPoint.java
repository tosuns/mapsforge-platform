/**
 * *****************************************************************************
 * Copyright 2013 Johannes Mitlmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
package de.fub.agg2graph.structs;

import java.util.ArrayList;
import java.util.Date;

/**
 * A gps point with some useful constructors additionally to the basic
 * functionality inherited from {@link AbstractLocation}.
 *
 * @author Johannes Mitlmeier
 *
 */
public class GPSPoint extends AbstractLocation {

    private Date timestamp;

    public GPSPoint() {
        super();
    }

    public GPSPoint(double lat, double lon) {
        super(lat, lon);
    }

    public GPSPoint(String ID, double lat, double lon) {
        super(ID, lat, lon);
    }

    public GPSPoint(ILocation location) {
        super(location);
    }

    public void setTimestamp(Date time) {
        this.timestamp = time;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * TODO MARTINUS
     *
     *
     * @param o
     * @return
     */
    public int compareLL(GPSPoint o) {
        if (this.getCluster() > o.getCluster()) {
            return 1;
        } else if (this.getCluster() < o.getCluster()) {
            return -1;
        } else if (getLat() > o.getLat()) {
            return 1;
        } else if (getLat() < o.getLat()) {
            return -1;
        } else if (getLon() > o.getLon()) {
            return 1;
        } else if (getLon() < o.getLon()) {
            return -1;
        }
        return 0;
    }

    public int compareTo(GPSPoint o) {
        if (compareLL(o) != 0) {
            return compareLL(o);
        } else if (this.elevation > o.getElevation()) {
            return 1;
        } else if (this.elevation < o.getElevation()) {
            return -1;
        }
        return 0;
    }

    @Override
    public int getCluster() {
        Integer cluster = (int) (Math.ceil(getLat()) * 361
                + Math.ceil(getLon()) + 180);
        return cluster;
    }

    public ArrayList<Integer> getNearClusters() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int cluster = this.getCluster();

        list.add(cluster);

        double modLat = getLat() % 1;
        double modLong = getLon() % 1;

        if (modLat < (0.1)) {

            list.add(cluster - 361);

            if (modLong < (0.1)) {
                list.add(cluster - 362);
            } else if (modLong > (0.9)) {
                list.add(cluster - 360);
            }

        } else if (modLat > (0.9)) {
            list.add(cluster + 361);

            if (modLong < (0.1)) {
                list.add(cluster + 360);
            } else if (modLong > (0.9)) {
                list.add(cluster + 362);
            }
        }
        if (modLong < (0.1)) {
            list.add(cluster - 1);
        } else if (modLong > (0.9)) {
            list.add(cluster + 1);
        }

        return list;
    }

    public double getDistanceTo(GPSPoint to) {
        return Math.sqrt(this.getSquaredDistanceTo(to));
    }

    public double getSquaredDistanceTo(GPSPoint to) {
        double deltaLat = getLat() - to.getLat();
        double deltaLong = getLon() - to.getLon();
        return deltaLat * deltaLat + deltaLong * deltaLong;
    }

    @Override
    public String toString() {
        return this.getLat() + "|" + this.getLon();
    }

    public static GPSPoint min(GPSPoint a, GPSPoint b) {
        return (a.compareTo(b) <= 0) ? a : b;
    }

    public static GPSPoint max(GPSPoint a, GPSPoint b) {
        return (a.compareTo(b) >= 0) ? a : b;
    }
}
