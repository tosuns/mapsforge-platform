package de.fub.agg2graph.gpseval.data;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a single waypoint of a GPS-track.
 */
public class Waypoint {

    private static final Logger LOG = Logger.getLogger(Waypoint.class.getName());
    public static final String PROP_NAME_SPEED = "waypoint.speed";
    public static final String PROP_NAME_BEARING = "waypoint.bearing";
    public static final String PROP_NAME_SEGEMENTS = "waypoint.segments";
    public static final String PROP_NAME_PRECISION = "waypoint.precision";
    public static final String PROP_NAME_TIMESTAMP = "waypoint.timestemp";
    public static final String PROP_NAME_LATITUDE = "waypoint.latitude";
    public static final String PROP_NAME_LONGITUDE = "waypoint.longitude";
    private Collection<String> propertyList = getPropertyList();
    private final HashMap<String, String> propertyMap = new HashMap<String, String>();
    protected double speed;
    protected double bearing;
    protected int segment;
    protected int precision;
    protected Date timestamp;
    protected double lat;
    protected double lon;

    public Waypoint() {
    }

    public Waypoint(Map<String, String> propertyMap) {
        this.propertyMap.putAll(propertyMap);
        init();
    }

    private void init() {
        for (Entry<String, String> entry : this.propertyMap.entrySet()) {
            if (entry.getValue() != null) {
                try {
                    switch (entry.getKey()) {
                        case PROP_NAME_BEARING:
                            bearing = Double.parseDouble(entry.getValue().replaceFirst(",", "."));
                            break;
                        case PROP_NAME_LATITUDE:
                            lat = Double.parseDouble(entry.getValue().replaceFirst(",", "."));
                            break;
                        case PROP_NAME_LONGITUDE:
                            lon = Double.parseDouble(entry.getValue().replaceFirst(",", "."));
                            break;
                        case PROP_NAME_PRECISION:
                            Integer.parseInt(entry.getValue());
                            break;
                        case PROP_NAME_SEGEMENTS:
                            segment = Integer.parseInt(entry.getValue());
                            break;
                        case PROP_NAME_SPEED:
                            speed = Double.parseDouble(entry.getValue().replaceFirst(",", "."));
                            break;
                        case PROP_NAME_TIMESTAMP:
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
                            try {
                                timestamp = sdf.parse(entry.getValue().replaceFirst("T", " "));
                            } catch (ParseException ex) {
                                LOG.log(Level.SEVERE, "Error parsing Date: {0}", ex.getMessage());
                            }
                            break;
                        default:
                            LOG.info(MessageFormat.format("Property ({0}) not supported!", entry.getKey())); //NO18N
                    }
                } catch (IllegalArgumentException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     *
     * @param data
     * @deprecated use instead {@link Waypoint(Map<String, String> propertyMap)
     */
    @Deprecated
    public Waypoint(String[] data) {
        speed = Double.parseDouble(data[7].replaceFirst(",", "."));
        segment = Integer.parseInt(data[0]);
        precision = Integer.parseInt(data[6]);
        bearing = Double.parseDouble(data[5].replaceFirst(",", "."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
        try {
            timestamp = sdf.parse(data[8].replaceFirst("T", " "));
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error parsing Date: {0}", ex.getMessage());
        }
        lat = Double.parseDouble(data[2]);
        lon = Double.parseDouble(data[3]);

    }

    public double getSpeed() {
        return speed;
    }

    public double getBearing() {
        return bearing;
    }

    public int getSegment() {
        return segment;
    }

    public int getPrecision() {
        return precision;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String putProperty(String propertyName, String value) {
        return propertyMap.put(propertyName, value);
    }

    public String getPropertyValue(String propertyName) {
        return propertyMap.get(propertyName);
    }

    public static Collection<String> createPropertyList(Class<? extends Waypoint> clazz) {
        Collection<String> collection = new ArrayList<String>();
        Waypoint waypoint;
        try {
            waypoint = clazz.newInstance();
            collection = waypoint.getPropertyList();
        } catch (InstantiationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return collection;
    }

    public Collection<String> getPropertyList() {
        if (propertyList == null) {
            propertyList = Arrays.asList(PROP_NAME_BEARING,
                    PROP_NAME_LATITUDE,
                    PROP_NAME_LONGITUDE,
                    PROP_NAME_PRECISION,
                    PROP_NAME_SEGEMENTS,
                    PROP_NAME_SPEED,
                    PROP_NAME_TIMESTAMP);
        }
        return propertyList;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.speed) ^ (Double.doubleToLongBits(this.speed) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.bearing) ^ (Double.doubleToLongBits(this.bearing) >>> 32));
        hash = 41 * hash + this.segment;
        hash = 41 * hash + this.precision;
        hash = 41 * hash + Objects.hashCode(this.timestamp);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Waypoint other = (Waypoint) obj;
        if (Double.doubleToLongBits(this.speed) != Double.doubleToLongBits(other.speed)) {
            return false;
        }
        if (Double.doubleToLongBits(this.bearing) != Double.doubleToLongBits(other.bearing)) {
            return false;
        }
        if (this.segment != other.segment) {
            return false;
        }
        if (this.precision != other.precision) {
            return false;
        }
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        return true;
    }
}
