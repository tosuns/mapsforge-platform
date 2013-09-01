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

import de.fub.agg2graph.agg.tiling.TileManager;
import de.fub.agg2graph.structs.projection.OsmProjection;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;
import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * Base class with common methods and attributes for all classes modelling
 * different types of locations throughout the codebase.
 *
 * @author Johannes Mitlmeier
 *
 */
public class AbstractLocation implements ILocation, Hideable {

    private static final Logger LOG = Logger.getLogger(AbstractLocation.class.getName());
    protected String ID;
    protected boolean visible = true;
    protected double[] latlon = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
    private final double EPSILON = 10e-6;
    protected double[] xy = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};

    public AbstractLocation() {
        super();
    }

    public AbstractLocation(double lat, double lon) {
        super();
        setLatLon(lat, lon);
    }

    public AbstractLocation(String ID, double lat, double lon) {
        super();
        setLatLon(lat, lon);
        this.ID = ID;
    }

    public AbstractLocation(ILocation location) {
        if (location != null) {
            this.setID(location.getID());
            this.setLatLon(location.getLat(), location.getLon());
        }
    }

    public AbstractLocation(GeoPosition position) {
        this.setLatLon(position.getLatitude(), position.getLongitude());
    }

    @Override
    public String toString() {
        if (ID == null) {
            return String.format(Locale.ENGLISH, "%s [lat: %.15f, lon: %.15f]",
                    this.getClass().getSimpleName(), latlon[0], latlon[1]);
        } else {
            return String
                    .format("%s <%s>", this.getClass().getSimpleName(), ID);
        }
    }

    @Override
    public String toDebugString() {
        return String.format("%s [id: %s, lat: %.15f, lon: %.15f]", this
                .getClass().getSimpleName(), ID, latlon[0], latlon[1]);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(latlon);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractLocation other = (AbstractLocation) obj;
        return Math.abs(this.getLat() - other.getLat()) <= EPSILON
                && Math.abs(this.getLon() - other.getLon()) <= EPSILON;
    }

    @Override
    public void setLat(double lat) {
        if (latlon[0] != lat) {
            latlon[0] = lat;
            xy[1] = OsmProjection.LatToY(latlon[0], OsmProjection.ZOOM_LEVEL);
        }
    }

    @Override
    public void setLon(double lon) {
        if (latlon[1] != lon) {
            latlon[1] = lon;
            xy[0] = OsmProjection.LonToX(latlon[1], OsmProjection.ZOOM_LEVEL);
        }
    }

    @Override
    public void setLatLon(double lat, double lon) {
        // test parameters
        if (lat > TileManager.WORLD.getMaxY()
                || lat < TileManager.WORLD.getMinY()
                || lon < TileManager.WORLD.getMinX()
                || lon > TileManager.WORLD.getMaxX()) {
            LOG.info(String.valueOf(lat));
            LOG.info(String.valueOf(TileManager.WORLD.getMinY()));
            LOG.info(String.valueOf(TileManager.WORLD.getMaxY()));
            LOG.info(String.valueOf(lon));
            LOG.info(String.valueOf(TileManager.WORLD.getMinX()));
            LOG.info(String.valueOf(TileManager.WORLD.getMaxX()));
            LOG.info("GPSPoint outside of the world");
            return;
        }
        if (latlon[0] != lat || latlon[1] != lon) {
            latlon[0] = lat;
            latlon[1] = lon;
            toXY();
        }
    }

    private void toLatLon() {
        latlon[0] = OsmProjection.YToLat((int) xy[1], OsmProjection.ZOOM_LEVEL);
        latlon[1] = OsmProjection.XToLon((int) xy[0], OsmProjection.ZOOM_LEVEL);
    }

    private void toXY() {
        xy[0] = OsmProjection.LonToX(latlon[1], OsmProjection.ZOOM_LEVEL);
        xy[1] = OsmProjection.LatToY(latlon[0], OsmProjection.ZOOM_LEVEL);
    }

    @Override
    public void setX(double x) {
        if (xy[0] != x) {
            xy[0] = x;
            toLatLon();
        }
    }

    @Override
    public void setY(double y) {
        if (xy[1] != y) {
            xy[1] = y;
            toLatLon();
        }
    }

    @Override
    public void setXY(double x, double y) {
        if (xy[0] != x || xy[1] != y) {
            xy[0] = x;
            xy[1] = y;
            toLatLon();
        }
    }

    @Override
    public double getLat() {
        if (latlon[0] == Double.MAX_VALUE && latlon[1] == Double.MAX_VALUE) {
            toLatLon();
        }
        return latlon[0];
    }

    @Override
    public double getLon() {
        if (latlon[0] == Double.MAX_VALUE && latlon[1] == Double.MAX_VALUE) {
            toLatLon();
        }
        return latlon[1];
    }

    @Override
    public double[] getLatLon() {
        if (latlon[0] == Double.MAX_VALUE && latlon[1] == Double.MAX_VALUE) {
            toLatLon();
        }
        return latlon;
    }

    @Override
    public double getX() {
        if (xy[0] == Double.MAX_VALUE && xy[1] == Double.MAX_VALUE) {
            toXY();
        }
        return xy[0];
    }

    @Override
    public double getY() {
        if (xy[0] == Double.MAX_VALUE && xy[1] == Double.MAX_VALUE) {
            toXY();
        }
        return xy[1];
    }

    @Override
    public double[] getXY() {
        if (xy[0] == Double.MAX_VALUE && xy[1] == Double.MAX_VALUE) {
            toXY();
        }
        return xy;
    }

    @Override
    public void setLatLon(double[] latlon) {
        setLatLon(latlon[0], latlon[1]);
    }

    @Override
    public void setXY(double[] xy) {
        setXY(xy[0], xy[1]);
    }

    @Override
    public double getWeight() {
        return 2;
    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}
