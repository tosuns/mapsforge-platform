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

import de.fub.agg2graph.agg.AggConnection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * An edge in the gps data structures.
 *
 * @author Johannes Mitlmeier
 *
 */
public class GPSEdge extends AbstractEdge<GPSPoint> implements Comparable<GPSEdge> {

    public GPSEdge() {
        super();
    }

    public GPSEdge(GPSPoint from, GPSPoint to) {
        this.setFrom(from);
        this.setTo(to);
    }

    /**
     * Constructor from GPSPoint and distance
     *
     * @param from
     * @param to
     * @param distance
     */
    public GPSEdge(GPSPoint from, GPSPoint to, float distance) {
        super(from, to, distance);
    }

    /**
     * Constructor from AggConnection
     *
     * @param aggConnection
     */
    public GPSEdge(AggConnection aggConnection) {
        this.setFrom(aggConnection.getFrom());
        this.setTo(aggConnection.getTo());
    }

    /**
     * TODO MARTINUS
     *
     *
     * @return
     */
    public GPSRegion getRegion() {
        return new GPSRegion(getFrom(), getTo());
    }

    @Override
    public float getDistance() {
        if (distance == 0) {
            this.distance = (float) getFrom().getDistanceTo(getTo());
        }

        return distance;
    }

    public GPSPoint at(double t) {
        return new GPSPoint((1 - t) * from.getLat() + t * to.getLat(), (1 - t)
                * from.getLon() + t * to.getLon());
    }

    public double getNearestDistance(GPSEdge ed) {
        double gradient1 = this.getGradient();
        double gradient2 = ed.getGradient();
        double interception1 = this.getInterception();
        double interception2 = ed.getInterception();
        double sliceX = (interception1 - interception2)
                / (gradient2 - gradient1);
        if ((this.getTo().getLon() < sliceX && sliceX < this.getFrom().getLon())
                || (this.getTo().getLon() > sliceX && sliceX > this.getFrom()
                .getLon())) {
            return (0.0);
        } else {
            TreeSet<Double> distances = retrieveDistances(ed);
            return distances.first();
        }
    }

    public double getFarestDistance(GPSEdge ed) {
        TreeSet<Double> distances = retrieveDistances(ed);
        return distances.last();
    }

    public double getAverageDistance(GPSEdge ed) {
        TreeSet<Double> distances = retrieveDistances(ed);
        Iterator<Double> distIt = distances.iterator();
        double avg = 0.0;
        int cnt = 0;
        while (distIt.hasNext()) {
            avg = (avg * cnt + distIt.next()) / (cnt + 1);
            cnt++;
        }
        return avg;
    }

    public TreeSet<Double> retrieveDistances(GPSEdge ed) {
        double gradient1;
        double gradient2;
        double interception1;
        double interception2;
        double sliceX;
        gradient1 = this.getGradient();
        gradient2 = ed.getGradient();
        interception1 = this.getInterception();
        interception2 = ed.getInterception();
        double interceptionFrom1Per2 = this.getFrom().getLat()
                - this.getFrom().getLon() * (1 / gradient2);
        double interceptionFrom2Per1 = ed.getFrom().getLat()
                - ed.getFrom().getLon() * (1 / gradient1);
        double interceptionTo1Per2 = this.getTo().getLat()
                - this.getTo().getLon() * (1 / gradient2);
        double interceptionTo2Per1 = ed.getTo().getLat() - ed.getTo().getLon()
                * (1 / gradient1);
        TreeSet<Double> distances = new TreeSet<Double>();
        sliceX = (interceptionFrom1Per2 - interception2)
                / (gradient2 - (1 / gradient2));
        if ((ed.getTo().getLon() < sliceX && sliceX < ed.getFrom().getLon())
                || (ed.getTo().getLon() > sliceX && sliceX > ed.getFrom()
                .getLon())) {
            double latSlice = interceptionFrom1Per2 + (1 / gradient2) * sliceX;
            double latDist = Math.abs(latSlice - this.getFrom().getLat());
            double longDist = Math.abs(sliceX - this.getFrom().getLon());
            double dist = Math.sqrt(latDist * latDist + longDist * longDist);
            distances.add(dist);
        }
        sliceX = (interceptionFrom2Per1 - interception1)
                / (gradient1 - (1 / gradient1));
        if ((this.getTo().getLon() < sliceX && sliceX < this.getFrom().getLon())
                || (this.getTo().getLon() > sliceX && sliceX > this.getFrom()
                .getLon())) {
            double latSlice = interceptionFrom2Per1 + (1 / gradient1) * sliceX;
            double latDist = Math.abs(latSlice - ed.getFrom().getLat());
            double longDist = Math.abs(sliceX - ed.getFrom().getLon());
            double dist = Math.sqrt(latDist * latDist + longDist * longDist);
            distances.add(dist);
        }
        sliceX = (interceptionTo1Per2 - interception2)
                / (gradient2 - (1 / gradient2));
        if ((ed.getTo().getLon() < sliceX && sliceX < ed.getFrom().getLon())
                || (ed.getTo().getLon() > sliceX && sliceX > ed.getFrom()
                .getLon())) {
            double latSlice = interceptionTo1Per2 + (1 / gradient2) * sliceX;
            double latDist = Math.abs(latSlice - this.getTo().getLat());
            double longDist = Math.abs(sliceX - this.getTo().getLon());
            double dist = Math.sqrt(latDist * latDist + longDist * longDist);
            distances.add(dist);
        }
        sliceX = (interceptionTo2Per1 - interception1)
                / (gradient1 - (1 / gradient1));
        if ((this.getTo().getLon() < sliceX && sliceX < this.getFrom().getLon())
                || (this.getTo().getLon() > sliceX && sliceX > this.getFrom()
                .getLon())) {
            double latSlice = interceptionTo2Per1 + (1 / gradient1) * sliceX;
            double latDist = Math.abs(latSlice - ed.getTo().getLat());
            double longDist = Math.abs(sliceX - ed.getTo().getLon());
            double dist = Math.sqrt(latDist * latDist + longDist * longDist);
            distances.add(dist);
        }
        double latDist = Math.abs(this.getTo().getLat() - ed.getTo().getLat());
        double longDist = Math.abs(this.getTo().getLon() - ed.getTo().getLon());
        double dist = Math.sqrt(latDist * latDist + longDist * longDist);
        distances.add(dist);
        latDist = Math.abs(this.getTo().getLat() - ed.getFrom().getLat());
        longDist = Math.abs(this.getTo().getLon() - ed.getFrom().getLon());
        dist = Math.sqrt(latDist * latDist + longDist * longDist);
        distances.add(dist);
        latDist = Math.abs(this.getFrom().getLat() - ed.getTo().getLat());
        longDist = Math.abs(this.getFrom().getLon() - ed.getTo().getLon());
        dist = Math.sqrt(latDist * latDist + longDist * longDist);
        distances.add(dist);
        latDist = Math.abs(this.getFrom().getLat() - ed.getFrom().getLat());
        longDist = Math.abs(this.getFrom().getLon() - ed.getFrom().getLon());
        dist = Math.sqrt(latDist * latDist + longDist * longDist);
        distances.add(dist);
        return distances;
    }

    public double getGradient() {
        return (this.getFrom().getLat() - this.getTo().getLat())
                / (this.getFrom().getLon() - this.getTo().getLon());
    }

    public double getAngle() {
        double angle = Math.toDegrees(Math.atan((this.getEuDistance()
                * this.getEuDistance() + this.getLatDistance()
                * this.getLatDistance() - this.getLongDistance()
                * this.getLongDistance())
                / (2 * this.getEuDistance() * this.getLatDistance())));
        angle = Math.toDegrees(Math.atan(this.getLatDistance()
                / this.getLongDistance()));
        if (this.getTo().getLat() > this.getFrom().getLat()) {
            if (this.getTo().getLon() > this.getFrom().getLon()) {
                // correction on angle in order to have full 360 degrees: 90
                // minus angle
                angle = 90 - angle;
            } else {
                // 270 plus angle
                angle = 270 + angle;
            }
        } else {
            if (this.getTo().getLon() > this.getFrom().getLon()) {
                // 180 minus angle
                angle = 90 + angle;
            } else {
                // 180 plus angle
                angle = 270 - angle;
            }
        }
        return angle;
    }

    private double getLatDistance() {
        return Math.abs(this.getTo().getLat() - this.getFrom().getLat());
    }

    private double getLongDistance() {
        return Math.abs(this.getTo().getLon() - this.getFrom().getLon());
    }

    public double getInterception() {
        return this.getFrom().getLat() - this.getFrom().getLon()
                * this.getGradient();
    }

    @Override
    public String toString() {
        return this.getFrom() + "->" + this.getTo();
    }

    public Double getEuDistance() {
        return this.getFrom().getDistanceTo(this.getTo());
    }

    public double getSquaredEulerDistance() {
        return this.getFrom().getSquaredDistanceTo(this.getTo());
    }

    public GPSEdge[] divide(GPSPoint middle) {
        GPSEdge[] ret = new GPSEdge[2];
        //TODO check whether middle is in line
        setTo(middle);
        ret[0] = this;
        ret[1] = new GPSEdge(middle, to);
        return ret;
    }

    @Override
    public int compareTo(GPSEdge o) {
        if (o == null) {
            throw new NullPointerException();
        }

        int r = getFrom().compareTo(o.getFrom());
        if (r == 0) {
            return getTo().compareTo(o.getTo());
        } else {
            return r;
        }
    }
}
