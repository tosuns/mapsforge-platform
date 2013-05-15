/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.agg2graph.osm.OsmExporter;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.layers.GPSSegmentLayer;
import de.fub.agg2graphui.layers.Line;
import de.fub.agg2graphui.layers.MapMatchingLayer;
import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforgeplatform.openstreetmap.service.OpenstreetMapService;
import de.fub.mapsforgeplatform.openstreetmap.xml.osm.Nd;
import de.fub.mapsforgeplatform.openstreetmap.xml.osm.Node;
import de.fub.mapsforgeplatform.openstreetmap.xml.osm.Osm;
import de.fub.mapsforgeplatform.openstreetmap.xml.osm.Way;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
public class OSMEvaluatorProcess extends AbstractAggregationProcess<RoadNetwork, RoadNetwork> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/plugins/tasks/eval/datasourceProcessIcon.png";
    private static final String PROP_NAME_MAP_MATCH_INSTANCE = "osm.Evaluator.mapmatching.instance";
    private static final Logger LOG = Logger.getLogger(OSMEvaluatorProcess.class.getName());
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private static final String SECTION_PROPERTIES_NAME = "Properties";
    private static final String PROPERTY_SET_MAP_MATCHER = "Map-Matcher";
    private RoadNetwork roadNetwork;
    private final GPSSegmentLayer aggregatorRoadLayer = new GPSSegmentLayer("Aggregator Road Network", new RenderingOptions());
    private final GPSSegmentLayer osmRoadLayer = new GPSSegmentLayer("OSM Road Network", new RenderingOptions());
    private final MapMatchingLayer matchingLayer = new MapMatchingLayer("Map Matching Layer", new RenderingOptions());
    private final OpenstreetMapService openstreetMapService = new OpenstreetMapService();

    public OSMEvaluatorProcess() {
        super(null);
    }

    public OSMEvaluatorProcess(Aggregator aggregator) {
        super(aggregator);

        osmRoadLayer.getRenderingOptions().setColor(Color.green);
        getLayers().add(osmRoadLayer);
        aggregatorRoadLayer.getRenderingOptions().setColor(Color.blue);
        getLayers().add(aggregatorRoadLayer);
        matchingLayer.getRenderingOptions().setColor(Color.red);
        getLayers().add(matchingLayer);
    }

    @Override
    protected void start() {

        // clear layers
        aggregatorRoadLayer.clearRenderObjects();
        osmRoadLayer.clearRenderObjects();
        matchingLayer.clearRenderObjects();
        int processUnit = 0;

        if (roadNetwork != null) {

            ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
            handle.start(roadNetwork.roads.size());

            try {


                // convert specified roadnetwork to osm format
                // its necessary because through the export of the road network to
                // osm format the roadnetwork gets simplified
                Osm roadNetworkToOsm = convertRoadNetworkToOsm();

                if (roadNetworkToOsm != null) {

                    // convert osm to a more convenient respresentation
                    List<GPSSegment> roadGPSSegmentList = convertOsmToGPSSegments(roadNetworkToOsm);
                    addGPSSegmentsToLayer(roadGPSSegmentList, aggregatorRoadLayer);
                    List<GPSSegment> osmRoadNetwork = new ArrayList<GPSSegment>();

//                    for (GPSSegment gpsSegment : roadGPSSegmentList) {
                    // fetch the osm map that is covered by the gpssegments bounding box
                    // we need this approach, because osm has a 5000 nodes limit per request
                    // we try to minimize the bounding box by using the bounding box
                    // of the segment instead of the whole road network
                    Osm osmMap = getOSMMap(roadGPSSegmentList);

                    if (osmMap != null) {
                        List<GPSSegment> osmGPSSegmentList = convertOsmToGPSSegments(osmMap);
                        if (osmGPSSegmentList != null && !osmGPSSegmentList.isEmpty()) {
                            osmRoadNetwork.addAll(osmGPSSegmentList);
                            addGPSSegmentsToLayer(osmGPSSegmentList, osmRoadLayer);
                        }
                    }
                    processUnit++;
                    handle.progress(Math.min(100, processUnit / 2));

                    fireProcessProgressEvent(
                            new ProcessPipeline.ProcessEvent<OSMEvaluatorProcess>(
                            this,
                            "evaluation", Math.min(100, (int) ((100d / roadNetwork.roads.size()) * (processUnit)))));
//                    }

                    openstreetMapService.close();

                    MapMatcher mapMatcher = getMapMatcher();

                    if (mapMatcher != null && !osmRoadNetwork.isEmpty()) {
                        double cost = -1;
                        double count = 0;
                        List<MapMatcher.MapMatchSegment> matchedRoadNetwork = mapMatcher.findMatch(roadGPSSegmentList, osmRoadNetwork);
                        if (matchedRoadNetwork != null && !matchedRoadNetwork.isEmpty()) {
                            for (MapMatcher.MapMatchSegment matchedSegment : matchedRoadNetwork) {
                                // get the average cost of the matched segment
                                cost += matchedSegment.getMapMatchCost();
                                count++;
                                for (MapMatcher.MapMatchResult match : matchedSegment.getSegment()) {
                                    Line line = new Line(
                                            match.getTobeMatchedPoint(),
                                            match.getMatchedPoint(),
                                            matchingLayer.getRenderingOptions(),
                                            1);
                                    line.setLabel(String.valueOf(match.getDistance()));

                                    matchingLayer.add(line);
                                }
                            }
                        }

                        NotifyDescriptor.Message nd = new NotifyDescriptor.Message(MessageFormat.format("Map-Matcher cost amounts to {0} meters", count > 0 ? cost / count : 0));
                        DialogDisplayer.getDefault().notifyLater(nd);

                    }
                    handle.progress(100);
                }
            } finally {
                handle.finish();
            }
        }
    }

    private Osm getOSMMap(List<GPSSegment> roadNetwork) {
        Rectangle2D boundingBox = getBoundingBox(roadNetwork);

        double leftLong = boundingBox.getMinX();
        double bottomLat = boundingBox.getMinY();
        double rightLong = boundingBox.getMaxX();
        double topLat = boundingBox.getMaxY();

        Osm osmMap = null;


        try {
            osmMap = openstreetMapService.getOSMHighwayMap(Osm.class,
                    String.valueOf(leftLong), //leftlong
                    String.valueOf(bottomLat), // bottomlat
                    String.valueOf(rightLong), //rightlon
                    String.valueOf(topLat)); //toplat
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return osmMap;
    }

    private Rectangle2D getBoundingBox(List<GPSSegment> gpsSegments) {
        Area area = new Area();

        Rectangle2D point = null;
        for (GPSSegment gpsSegment : gpsSegments) {
            for (ILocation coordinate : gpsSegment) {
                point = new Rectangle2D.Double(coordinate.getLon(), coordinate.getLat(), 0.00000001, 0.00000001);
                area.add(new Area(point));
            }
        }
        return area.getBounds2D();
    }

    private void addGPSSegmentsToLayer(List<GPSSegment> gpsSegmentList, GPSSegmentLayer layer) {
        for (GPSSegment segment : gpsSegmentList) {
            layer.add(segment);
        }
    }

    @SuppressWarnings("unchecked")
    private MapMatcher getMapMatcher() {
        MapMatcher matcher = null;
        if (getProcessDescriptor() != null) {
            List<PropertySection> sections = getProcessDescriptor().getProperties().getSections();
            for (PropertySection section : sections) {
                if (SECTION_PROPERTIES_NAME.equals(section.getName())) {
                    List<PropertySet> propertySets = section.getPropertySet();
                    for (PropertySet propertySet : propertySets) {
                        if (PROPERTY_SET_MAP_MATCHER.equals(propertySet.getName())) {
                            List<Property> properties = propertySet.getProperties();


                            for (Property property : properties) {
                                if (PROP_NAME_MAP_MATCH_INSTANCE.equals(property.getId())) {
                                    try {
                                        Collection<? extends MapMatcher> allInstances = Lookup.getDefault().lookupResult(MapMatcher.class).allInstances();
                                        for (MapMatcher mapMatcher : allInstances) {
                                            String mapMatcherName = mapMatcher.getClass().getName();
                                            String javaType = property.getValue();
                                            if (mapMatcherName.equals(javaType)) {
                                                matcher = mapMatcher.getClass().newInstance();
                                                break;
                                            }
                                        }
                                    } catch (InstantiationException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } catch (IllegalAccessException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return matcher;
    }

    private List<GPSSegment> convertOsmToGPSSegments(Osm osmRoadMap) {
        List<GPSSegment> gpsSegmentList = new ArrayList<GPSSegment>(500);

        List<Node> nodeList = osmRoadMap.getNodes();
        List<Way> wayList = osmRoadMap.getWays();

        GPSPoint point = null;

        for (Way way : wayList) {
            GPSSegment waySegment = new GPSSegment();
            for (Nd nd : way.getNds()) {

                for (Node node : nodeList) {
                    if (nd.getRef() == node.getId()) {
                        point = new GPSPoint(node.getLat(), node.getLon());
                        waySegment.add(point);
                    }
                }
            }
            // add non empty segments to segment list.
            if (!waySegment.isEmpty()) {
                gpsSegmentList.add(waySegment);
            }
        }
        return gpsSegmentList;
    }

    private Osm convertRoadNetworkToOsm() {
        Osm roadNetworkAsOsm = null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OsmExporter exporter = new OsmExporter();
        exporter.export(roadNetwork, outputStream);

        try {
            outputStream.close();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());



            try {
                JAXBContext jaxbCtx = JAXBContext.newInstance(Osm.class);
                Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                Object object = unmarshaller.unmarshal(inputStream); //NOI18N
                if (object instanceof Osm) {
                    roadNetworkAsOsm = (Osm) object;
                }
            } catch (javax.xml.bind.JAXBException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex); //NOI18N
            } finally {
                inputStream.close();
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex); //NOI18N
        }
        return roadNetworkAsOsm;
    }

    @Override
    public Image getIcon() {
        return IMAGE;
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @NbBundle.Messages("CLT_OSMEvaluatorProcess_Name=OSM Map Evaluator")
    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return Bundle.CLT_OSMEvaluatorProcess_Name();
    }

    @NbBundle.Messages("CLT_OSMEvaluatorProcess_Description=A map evaluator which uses osm map to evaluator")
    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_OSMEvaluatorProcess_Description();
    }

    @Override
    public void setInput(RoadNetwork input) {
        this.roadNetwork = input;
    }

    @Override
    public RoadNetwork getResult() {
        RoadNetwork resultRoadNetwork = roadNetwork;
        roadNetwork = null;
        return resultRoadNetwork;
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return canceled.get();
    }

    @Override
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException {
        List<StatisticSection> statisticData = new ArrayList<StatisticSection>();

        // create process performance statistics
        StatisticSection section = getPerformanceData();
        statisticData.add(section);

        return statisticData;
    }

    @Override
    public Component getVisualRepresentation() {
        return null;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor processDescriptor = new ProcessDescriptor();
        processDescriptor
                .setJavaType(OSMEvaluatorProcess.class
                .getName());
        processDescriptor.setDisplayName(Bundle.CLT_OSMEvaluatorProcess_Name());
        processDescriptor.setDescription(Bundle.CLT_OSMEvaluatorProcess_Description());
        Property property = new Property("class", PointToPointMapMatcher.class.getName());

        property.setId(PROP_NAME_MAP_MATCH_INSTANCE);

        property.setJavaType(String.class
                .getName());
        property.setDescription(
                "The instance which is responsible to find a match.");

        PropertySet propertySet = new PropertySet(PROPERTY_SET_MAP_MATCHER, "Properties concering the map matcher instance.");

        propertySet.getProperties()
                .add(property);

        PropertySection propertySection = new PropertySection(SECTION_PROPERTIES_NAME, "Properties of this Evaluator process.");

        propertySection.getPropertySet()
                .add(propertySet);
        propertySection.getPropertySet();
        List<PropertySection> sections = processDescriptor.getProperties().getSections();

        sections.add(propertySection);
        return processDescriptor;
    }
}
