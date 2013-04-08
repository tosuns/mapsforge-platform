/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.converter;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.gpxmodule.xml.gpx.Trk;
import de.fub.gpxmodule.xml.gpx.Trkseg;
import de.fub.gpxmodule.xml.gpx.Wpt;
import de.fub.mapsforge.project.detector.model.gpx.GpxWayPoint;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.utils.GPSUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = DataConverter.class)
public class GpxConverter implements DataConverter {

    @Override
    public boolean isFileTypeSupported(FileObject fileObject) {
        boolean result = false;

        if (fileObject != null) {
            if ("text/gpx+xml".equals(fileObject.getMIMEType())) {
                result = true;
            } else {
                result = "gpx".equalsIgnoreCase(fileObject.getExt());
            }
        }
        return result;
    }

    @Override
    public synchronized List<TrackSegment> convert(FileObject fileObject) throws DataConverterException {
        List<TrackSegment> trackSegmentList = new ArrayList<TrackSegment>();
        Gpx gpx = null;
        if (fileObject != null && isFileTypeSupported(fileObject)) {
            try {
                DataObject dataObject = DataObject.find(fileObject);
                if (dataObject instanceof GPXDataObject) {
                    gpx = ((GPXDataObject) dataObject).getGpx();
                    if (gpx == null) {
                        throw new DataConverterException(MessageFormat.format("Failed to convert specified file: {0}", fileObject.getPath()));
                    } else {
                        trackSegmentList = convertToTrackSegment(gpx);
                    }
                } else {
                    throw new DataConverterException("File type not type of GPXDataObject!");
                }
            } catch (DataObjectNotFoundException ex) {
                throw new DataConverterException("Couldn't convert file to gpx because !");
            }
        } else {
            throw new DataConverterException("Couldn't convert file gpx because file == null or file type is not supported by this converter!");
        }
        return trackSegmentList;
    }

    private List<TrackSegment> convertToTrackSegment(Gpx gpx) {
        List<TrackSegment> trackSegmentList = new ArrayList<TrackSegment>();

        for (Trk track : gpx.getTrk()) {
            for (Trkseg segment : track.getTrkseg()) {

                if (!segment.getTrkpt().isEmpty()) {
                    TrackSegment trackSegment = new TrackSegment();
                    Wpt lastWpt = null;
                    for (Wpt wpt : segment.getTrkpt()) {
                        Map<String, String> gpxWaypointPropertymap = createWaypointPropertymap(lastWpt, wpt);
                        GpxWayPoint gpxWayPoint1 = new GpxWayPoint(gpxWaypointPropertymap);
                        trackSegment.getWayPointList().add(gpxWayPoint1);
                    }
                    trackSegmentList.add(trackSegment);
                }
            }
        }

        return trackSegmentList;
    }

    protected Map<String, String> createWaypointPropertymap(Wpt previouseWpt, Wpt gpxWpt) {
        HashMap<String, String> map = new HashMap<String, String>();
        Collection<String> propertyList = new Waypoint().getPropertyList();
        for (String property : propertyList) {
            if (GpxWayPoint.PROP_NAME_BEARING.equals(property)) {
                // gpx version 1.1 does not support bearing information
            } else if (GpxWayPoint.PROP_NAME_LATITUDE.equals(property)) {
                map.put(Waypoint.PROP_NAME_LATITUDE, String.valueOf(gpxWpt.getLat().doubleValue()));
            } else if (GpxWayPoint.PROP_NAME_LONGITUDE.equals(property)) {
                map.put(Waypoint.PROP_NAME_LONGITUDE, String.valueOf(gpxWpt.getLon().doubleValue()));
            } else if (GpxWayPoint.PROP_NAME_PRECISION.equals(property)) {
                if (gpxWpt.getPdop() != null) {
                    map.put(Waypoint.PROP_NAME_PRECISION, String.valueOf(gpxWpt.getPdop().doubleValue()));
                }
            } else if (GpxWayPoint.PROP_NAME_SEGEMENTS.equals(property)) {
                //at this point of development we don't use segment number for filtering
            } else if (GpxWayPoint.PROP_NAME_SPEED.equals(property)) {
                if (gpxWpt.getLon() != null && gpxWpt.getLat() != null && gpxWpt.getTime() != null) {
                    map.put(Waypoint.PROP_NAME_SPEED, String.valueOf(GPSUtils.computeSpeed(previouseWpt, gpxWpt)));
                }
            } else if (GpxWayPoint.PROP_NAME_TIMESTAMP.equals(property)) {
                if (gpxWpt.getTime() != null) {
                    map.put(Waypoint.PROP_NAME_TIMESTAMP, String.valueOf(gpxWpt.getTime().getTime()));
                }
            } else if (GpxWayPoint.PROP_NAME_AGE_OF_DGPS_DATA.equals(property)) {
                if (gpxWpt.getAgeofdgpsdata() != null) {
                    map.put(GpxWayPoint.PROP_NAME_AGE_OF_DGPS_DATA, String.valueOf(gpxWpt.getAgeofdgpsdata().doubleValue()));
                }
            } else if (GpxWayPoint.PROP_NAME_DGPS_ID.equals(property)) {
                if (gpxWpt.getDgpsid() != null) {
                    map.put(GpxWayPoint.PROP_NAME_DGPS_ID, String.valueOf(gpxWpt.getDgpsid()));
                }
            } else if (GpxWayPoint.PROP_NAME_ELEVATION.equals(property)) {
                if (gpxWpt.getEle() != null) {
                    map.put(GpxWayPoint.PROP_NAME_ELEVATION, String.valueOf(gpxWpt.getEle().doubleValue()));
                }
            } else if (GpxWayPoint.PROP_NAME_FIX.equals(property)) {
                if (gpxWpt.getFix() != null) {
                    map.put(GpxWayPoint.PROP_NAME_FIX, gpxWpt.getFix().value());
                }
            } else if (GpxWayPoint.PROP_NAME_GEOID_HEIGHT.equals(property)) {
                if (gpxWpt.getGeoidheight() != null) {
                    map.put(GpxWayPoint.PROP_NAME_GEOID_HEIGHT, String.valueOf(gpxWpt.getGeoidheight().doubleValue()));
                }
            } else if (GpxWayPoint.PROP_NAME_HDOP.equals(property)) {
                if (gpxWpt.getHdop() != null) {
                    map.put(GpxWayPoint.PROP_NAME_HDOP, String.valueOf(gpxWpt.getHdop().doubleValue()));
                }
            } else if (GpxWayPoint.PROP_NAME_MAG_VAR.equals(property)) {
                if (gpxWpt.getMagvar() != null) {
                    map.put(GpxWayPoint.PROP_NAME_MAG_VAR, String.valueOf(gpxWpt.getMagvar().doubleValue()));
                }
            } else if (GpxWayPoint.PROP_NAME_PDOP.equals(property)) {
                if (gpxWpt.getPdop() != null) {
                    map.put(GpxWayPoint.PROP_NAME_PDOP, String.valueOf(gpxWpt.getPdop()));
                }
            } else if (GpxWayPoint.PROP_NAME_SATALLITES.equals(property)) {
                if (gpxWpt.getSat() != null) {
                    map.put(GpxWayPoint.PROP_NAME_SATALLITES, String.valueOf(gpxWpt.getSat()));
                }
            } else if (GpxWayPoint.PROP_NAME_VDOP.equals(property)) {
                if (gpxWpt.getVdop() != null) {
                    map.put(GpxWayPoint.PROP_NAME_VDOP, String.valueOf(gpxWpt.getVdop().doubleValue()));
                }
            }
        }
        return map;
    }
}
