/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.xml.Gpx;
import de.fub.gpxmodule.xml.ObjectFactory;
import de.fub.gpxmodule.xml.Trk;
import de.fub.gpxmodule.xml.Trkseg;
import de.fub.gpxmodule.xml.Wpt;
import de.fub.mapsforge.project.detector.model.gpx.GpxWayPoint;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters.MinimumTrackLengthFilterProcess;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters.MinimumWaypointFilterProcess;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters.ResegmentationFilterProcess;
import de.fub.mapsforge.project.detector.utils.GPSUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.netbeans.spi.xml.cookies.TransformableSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class ResegmentationTest {

    public ResegmentationTest() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void resegmentTest() {
        try {
            String FILEPATH = "C:/Users/Serdar/Documents/NetBeansProjects/Maps Forge Project/GPXDatasource/testFolder/Gpx_0.gpx";
            String DEST_FILEPATH = "C:/Users/Serdar/Documents/NetBeansProjects/Maps Forge Project/GPXDatasource/testFolder/Gpx_0.gpx";

            File sourceFile = new File(FILEPATH);
            if (!sourceFile.exists()) {
                throw new IOException();
            }

            Gpx sourceGpx = unmarshall(sourceFile);

            List<TrackSegment> trackSegmentList = convertToTrackSegment(sourceGpx);

            FilterProcess[] filters = new FilterProcess[]{new ResegmentationFilterProcess(), new MinimumWaypointFilterProcess(), new MinimumTrackLengthFilterProcess()};

            for (FilterProcess filter : filters) {
                filter.setInput(trackSegmentList);
                filter.run();
                trackSegmentList = filter.getResult();
            }

            Trk track = new Trk();
            for (TrackSegment trackSegment : trackSegmentList) {
                Trkseg trkseg = new Trkseg();

                for (Waypoint point : trackSegment.getWayPointList()) {
                    Wpt wpt = new Wpt();
                    wpt.setLat(new BigDecimal(point.getLat()));
                    wpt.setLon(new BigDecimal(point.getLon()));
                    wpt.setTime(point.getTimestamp());
                    trkseg.getTrkpt().add(wpt);
                }
                track.getTrkseg().add(trkseg);
            }

            Gpx gpx = new Gpx();
            gpx.getTrk().add(track);

            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(new ObjectFactory().createGpx(gpx), new File(DEST_FILEPATH));

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Assert.fail(ex.getMessage());
        }

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
        Collection<String> propertyList = new GpxWayPoint().getPropertyList();
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
                    map.put(Waypoint.PROP_NAME_SPEED, String.valueOf(GPSUtils.computeVelocity(previouseWpt, gpxWpt)));
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

    @SuppressWarnings("unchecked")
    private Gpx unmarshall(File sourceFile) throws IOException {
        InputStream inputStream = new FileInputStream(sourceFile);
        try {
            // parse gpx version 1.1
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            return ((JAXBElement<Gpx>) unmarshaller.unmarshal(inputStream)).getValue();
        } catch (Exception ex) {
            inputStream = new FileInputStream(sourceFile);
            // fall back track to parse as gpx vsrsion 1.1
            try {
                StringWriter stringWriter = new StringWriter();
                StreamResult streamResult = new StreamResult(stringWriter);

                InputStream resourceAsStream = GPXDataObject.class.getResourceAsStream("/de/fub/mapsforge/project/detector/gpx10to11Transformer.xsl");
                StreamSource streamSource = new StreamSource(resourceAsStream);
                TransformableSupport transformableSupport = new TransformableSupport(new StreamSource(inputStream));

                transformableSupport.transform(streamSource, streamResult, null);

                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

                Object unmarshal = unmarshaller.unmarshal(new StringReader(stringWriter.toString()));
                if (unmarshal instanceof JAXBElement<?>) {
                    return ((JAXBElement<Gpx>) unmarshal).getValue();
                } else if (unmarshal instanceof Gpx) {
                    return (Gpx) unmarshal;
                }
            } catch (TransformerException ex1) {
                Exceptions.printStackTrace(ex1);
            } catch (JAXBException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        } finally {
            inputStream.close();
        }
        return null;
    }
}
