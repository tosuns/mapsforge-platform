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
import static de.fub.agg2graph.gpseval.data.Waypoint.PROP_NAME_BEARING;
import static de.fub.agg2graph.gpseval.data.Waypoint.PROP_NAME_LATITUDE;
import static de.fub.agg2graph.gpseval.data.Waypoint.PROP_NAME_LONGITUDE;
import static de.fub.agg2graph.gpseval.data.Waypoint.PROP_NAME_PRECISION;
import static de.fub.agg2graph.gpseval.data.Waypoint.PROP_NAME_SEGEMENTS;
import static de.fub.agg2graph.gpseval.data.Waypoint.PROP_NAME_SPEED;
import de.fub.gpxmodule.xml.Fix;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Serdar
 */
public class GpxWayPoint extends Waypoint {

    private static final Logger LOG = Logger.getLogger(GpxWayPoint.class.getName());
    public static final String PROP_NAME_ELEVATION = "gpxWaypoint.elevation";
    public static final String PROP_NAME_MAG_VAR = "gpxWaypoint.magvar";
    public static final String PROP_NAME_GEOID_HEIGHT = "gpxWaypoint.geoidHeight";
    public static final String PROP_NAME_FIX = "gpxWaypoint.fix";
    public static final String PROP_NAME_SATALLITES = "gpxWaypoint.sat";
    public static final String PROP_NAME_HDOP = "gpxWaypoint.hdop";
    public static final String PROP_NAME_VDOP = "gpxWaypoint.vdop";
    public static final String PROP_NAME_PDOP = "gpxWaypoint.pdop";
    public static final String PROP_NAME_AGE_OF_DGPS_DATA = "gpxWaypoint.ageofdfpsdata";
    public static final String PROP_NAME_DGPS_ID = "gpxWaypoint.dgpsid";
    protected Double ageOfDgpsData = null;
    protected Integer dgpsId = null;
    protected Double elevation = null;
    protected Fix fix = null;
    protected Double geoidHeight = null;
    protected Double magVar = null;
    protected Double pDop = null;
    protected Integer satCount = null;
    protected Double vdop = null;
    protected Double hdop = null;

    public GpxWayPoint() {
    }

    public GpxWayPoint(Map<String, String> propertyMap) {
        for (Entry<String, String> entry : propertyMap.entrySet()) {
            putPropertyValue(entry.getKey(), entry.getValue());
            if (entry.getValue() != null) {
                try {
                    if (PROP_NAME_ELEVATION.equals(entry.getKey())) {
                        elevation = Double.parseDouble(entry.getValue());

                    } else if (PROP_NAME_MAG_VAR.equals(entry.getKey())) {
                        magVar = Double.parseDouble(entry.getValue());
                    } else if (PROP_NAME_GEOID_HEIGHT.equals(entry.getKey())) {
                        geoidHeight = Double.parseDouble(entry.getValue());
                    } else if (PROP_NAME_FIX.equals(entry.getKey())) {
                        fix = Fix.fromValue(entry.getValue());
                    } else if (PROP_NAME_SATALLITES.equals(entry.getKey())) {
                        satCount = Integer.parseInt(entry.getValue());
                    } else if (PROP_NAME_HDOP.equals(entry.getKey())) {
                        hdop = Double.parseDouble(entry.getValue());
                    } else if (PROP_NAME_VDOP.equals(entry.getKey())) {
                        vdop = Double.parseDouble(entry.getValue());
                    } else if (PROP_NAME_PDOP.equals(entry.getKey())) {
                        pDop = Double.parseDouble(entry.getValue());
                    } else if (PROP_NAME_AGE_OF_DGPS_DATA.equals(entry.getKey())) {
                        ageOfDgpsData = Double.parseDouble(entry.getValue());
                    } else if (PROP_NAME_DGPS_ID.equals(entry.getKey())) {
                        dgpsId = Integer.parseInt(entry.getValue());
                    } else if (PROP_NAME_TIMESTAMP.equals(entry.getKey())) {
                        timestamp = new Date(Long.parseLong(entry.getValue()));
                    } else if (PROP_NAME_BEARING.equals(entry.getKey())) {
                        bearing = Double.parseDouble(entry.getValue().replaceFirst(",", "."));

                    } else if (PROP_NAME_LATITUDE.equals(entry.getKey())) {
                        lat = Double.parseDouble(entry.getValue().replaceFirst(",", "."));

                    } else if (PROP_NAME_LONGITUDE.equals(entry.getKey())) {
                        lon = Double.parseDouble(entry.getValue().replaceFirst(",", "."));

                    } else if (PROP_NAME_PRECISION.equals(entry.getKey())) {
                        Integer.parseInt(entry.getValue());

                    } else if (PROP_NAME_SEGEMENTS.equals(entry.getKey())) {
                        segment = Integer.parseInt(entry.getValue());

                    } else if (PROP_NAME_SPEED.equals(entry.getKey())) {
                        speed = Double.parseDouble(entry.getValue().replaceFirst(",", "."));
                    }
                } catch (IllegalArgumentException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    public Double getAgeOfDgpsData() {
        return ageOfDgpsData;
    }

    public Integer getDgpsId() {
        return dgpsId;
    }

    public Double getElevation() {
        return elevation;
    }

    public Fix getFix() {
        return fix;
    }

    public Double getGeoidHeight() {
        return geoidHeight;
    }

    public Double getMagVar() {
        return magVar;
    }

    public Double getpDop() {
        return pDop;
    }

    public Integer getSatCount() {
        return satCount;
    }

    public Double getVdop() {
        return vdop;
    }

    public Double getHdop() {
        return hdop;
    }

    @Override
    public Collection<String> getPropertyList() {
        return Arrays.asList(
                PROP_NAME_AGE_OF_DGPS_DATA,
                PROP_NAME_DGPS_ID,
                PROP_NAME_ELEVATION,
                PROP_NAME_FIX,
                PROP_NAME_GEOID_HEIGHT,
                PROP_NAME_HDOP,
                PROP_NAME_LATITUDE,
                PROP_NAME_LONGITUDE,
                PROP_NAME_MAG_VAR,
                PROP_NAME_PDOP,
                PROP_NAME_SATALLITES,
                PROP_NAME_SPEED,
                PROP_NAME_TIMESTAMP,
                PROP_NAME_VDOP);
    }
}
