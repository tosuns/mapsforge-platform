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
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.plugins.mapmatcher.MapMatcher;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassProperty;
import de.fub.mapsforge.project.aggregator.factories.nodes.properties.ClassWrapper;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AggregationProcessNode;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforgeplatform.openstreetmap.service.MapProvider;
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
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
    private static final String PROP_NAME_MAP_PROVIDER_INSTANCE = "osm.evaluator.mapprovider.instance";
    private static final Logger LOG = Logger.getLogger(OSMEvaluatorProcess.class.getName());
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private final GPSSegmentLayer aggregatorRoadLayer = new GPSSegmentLayer("Aggregator Road Network", new RenderingOptions());
    private final GPSSegmentLayer osmRoadLayer = new GPSSegmentLayer("OSM Road Network", new RenderingOptions());
    private final MapMatchingLayer matchingLayer = new MapMatchingLayer("Map Matching Layer", new RenderingOptions());
    private final GPSSegmentLayer resultLayer = new GPSSegmentLayer("OSM Matched Road Network", new RenderingOptions());
    private RoadNetwork roadNetwork;
    private OSMEvaluatorProcessNode node;
    private MapMatcher mapMatcher;
    private MapProvider mapProvider;
    private double averageDistance;
    private double mappingCost;

    public OSMEvaluatorProcess() {
        osmRoadLayer.getRenderingOptions().setColor(Color.green);
        getLayers().add(osmRoadLayer);
        osmRoadLayer.getRenderingOptions().setzIndex(0);
        aggregatorRoadLayer.getRenderingOptions().setColor(Color.blue);
        getLayers().add(aggregatorRoadLayer);
        matchingLayer.getRenderingOptions().setColor(Color.red);
        getLayers().add(matchingLayer);
        resultLayer.getRenderingOptions().setColor(Color.cyan);
        getLayers().add(resultLayer);
    }

    @Override
    public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        super.setProcessDescriptor(processDescriptor);
        reInit();
    }

    private void reInit() {
        if (getProcessDescriptor() != null) {
            List<PropertySection> sections = getProcessDescriptor().getProperties().getSections();
            for (PropertySection section : sections) {
                if (OSMEvaluatorProcess.class.getName().equals(section.getId())) {
                    List<PropertySet> propertySets = section.getPropertySet();
                    for (PropertySet propertySet : propertySets) {
                        if (OSMEvaluatorProcess.class.getName().equals(propertySet.getId())) {
                            List<Property> properties = propertySet.getProperties();

                            for (Property property : properties) {
                                if (property.getValue() != null) {
                                    if (PROP_NAME_MAP_MATCH_INSTANCE.equals(property.getId())) {
                                        try {
                                            mapMatcher = MapMatcher.Factory.find(property.getValue());
                                        } catch (MapMatcher.MapMatcherNotFoundException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                        if (mapMatcher == null) {
                                            mapMatcher = MapMatcher.Factory.getDefault();
                                        }
                                    } else if (PROP_NAME_MAP_PROVIDER_INSTANCE.equals(property.getId())) {
                                        try {
                                            mapProvider = MapProvider.Factory.find(property.getValue());
                                        } catch (MapProvider.MapProviderNotFoundException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                        if (mapProvider == null) {
                                            mapProvider = MapProvider.Factory.getDefault();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public double getAvgMappingDistance() {
        return averageDistance;
    }

    public double getMappingCost() {
        return mappingCost;
    }

    public RoadNetwork getRoadNetwork() {
        return roadNetwork;
    }

    @Override
    protected void start() {

        // clear layers
        aggregatorRoadLayer.clearRenderObjects();
        osmRoadLayer.clearRenderObjects();
        matchingLayer.clearRenderObjects();
        resultLayer.clearRenderObjects();

        if (roadNetwork != null) {
            RoadNetwork network = roadNetwork;
            ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
            handle.start();

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

                    // fetch the osm map that is covered by the gpssegments bounding box
                    // we need this approach, because osm has a 5000 nodes limit per request
                    // we try to minimize the bounding box by using the bounding box
                    // of the segment instead of the whole road network
                    handle.setDisplayName("Downloading map...");
                    Osm osmMap = getOSMMap(roadGPSSegmentList);

                    if (osmMap != null) {
                        List<GPSSegment> osmGPSSegmentList = convertOsmToGPSSegments(osmMap);
                        if (osmGPSSegmentList != null && !osmGPSSegmentList.isEmpty()) {
                            osmRoadNetwork.addAll(osmGPSSegmentList);
                            addGPSSegmentsToLayer(osmGPSSegmentList, osmRoadLayer);
                        }
                    }


                    if (getMapMatcher() != null && !osmRoadNetwork.isEmpty()) {
                        double cost = -1;
                        double count = 0;
                        double sumDistance = 0;
                        List<MapMatcher.MapMatchSegment> matchedRoadNetwork = getMapMatcher().findMatch(roadGPSSegmentList, osmRoadNetwork);
                        if (matchedRoadNetwork != null && !matchedRoadNetwork.isEmpty()) {
                            for (MapMatcher.MapMatchSegment matchedSegment : matchedRoadNetwork) {
                                // get the average cost of the matched segment
                                cost += matchedSegment.getMapMatchCost();
                                count++;
                                GPSSegment resultSegment = new GPSSegment();

                                for (MapMatcher.MapMatchResult match : matchedSegment.getSegment()) {
                                    Line line = new Line(
                                            match.getTobeMatchedPoint(),
                                            match.getMatchedPoint(),
                                            matchingLayer.getRenderingOptions(),
                                            1);
                                    line.setLabel(String.valueOf(match.getDistance()));
                                    sumDistance += match.getDistance();
                                    matchingLayer.add(line);
                                    resultSegment.add(new GPSPoint(match.getMatchedPoint()));
                                }
                                resultLayer.add(resultSegment);
                            }
                        }
                        // cost of the map-matching in average distance unit
                        averageDistance = count > 0 ? cost / count : 0;

                        mappingCost = network.getTotalRoadLength() == 0 ? 0 : averageDistance / network.getTotalRoadLength();
                        LOG.log(Level.INFO, "average distance: {0}", averageDistance);
                        LOG.log(Level.INFO, "average distance/network length: {0}", mappingCost);
                        NotifyDescriptor.Message nd = new NotifyDescriptor.Message(
                                String.format(Locale.ENGLISH,
                                "Map-Matcher cost amounts to %f\nAverage Distance (m): %f\nSum length to networklength: %f",
                                mappingCost,
                                averageDistance,
                                sumDistance / roadNetwork.getTotalRoadLength()));
                        DialogDisplayer.getDefault().notifyLater(nd);

                    }
                }
            } finally {
                handle.finish();
            }
        }
    }

    private Osm getOSMMap(List<GPSSegment> roadNetwork) {
        Rectangle2D boundingBox = getBoundingBox(roadNetwork);
        double buffer = 0.000000025;
        double leftLong = boundingBox.getMinX() - buffer;
        double bottomLat = boundingBox.getMinY() - buffer;
        double rightLong = boundingBox.getMaxX() + buffer;
        double topLat = boundingBox.getMaxY() + buffer;

        Osm osmMap = null;

        try {
            osmMap = mapProvider.getMap(leftLong, bottomLat, rightLong, topLat);
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
        layer.addAll(gpsSegmentList);
    }

    private MapMatcher getMapMatcher() {
        return mapMatcher;
    }

    public void setMapMatcher(MapMatcher mapMatcher) {
        this.mapMatcher = mapMatcher;
    }

    public MapProvider getMapProvider() {
        return mapProvider;
    }

    public void setMapProvider(MapProvider mapProvider) {
        this.mapProvider = mapProvider;
    }

    private List<GPSSegment> convertOsmToGPSSegments(Osm osmRoadMap) {
        List<GPSSegment> gpsSegmentList = new ArrayList<GPSSegment>(500);

        List<Node> nodeList = osmRoadMap.getNodes();
        List<Way> wayList = osmRoadMap.getWays();

        GPSPoint point = null;

        for (Way way : wayList) {
            GPSSegment waySegment = new GPSSegment();
            for (Nd nd : way.getNds()) {

                for (Node n : nodeList) {
                    if (nd.getRef() == n.getId()) {
                        point = new GPSPoint(n.getLat(), n.getLon());
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

    @NbBundle.Messages({
        "CLT_OSMEvaluatorProcess_Property_MapMatcher_Name=Map-Matcher Instance",
        "CLT_OSMEvaluatorProcess_Property_MapMatcher_Description=The instance that is responsible for the matching of the points",
        "CLT_OSMEvaluatorProcess_Property_MapProvider_Name=Map-Provider Instance",
        "CLT_OSMEvaluatorProcess_Property_MapProvider_Description=Different types of map providers for the matching."
    })
    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor processDescriptor = new ProcessDescriptor();
        processDescriptor.setJavaType(OSMEvaluatorProcess.class.getName());
        processDescriptor.setDisplayName(Bundle.CLT_OSMEvaluatorProcess_Name());
        processDescriptor.setDescription(Bundle.CLT_OSMEvaluatorProcess_Description());

        PropertySet propertySet = new PropertySet();
        propertySet.setId(OSMEvaluatorProcess.class.getName());
        propertySet.setName("Properties");
        propertySet.setDescription("Properties concering the map matcher instance.");

        Property property = new Property();
        property.setId(PROP_NAME_MAP_MATCH_INSTANCE);
        property.setJavaType(String.class.getName());
        property.setName(Bundle.CLT_OSMEvaluatorProcess_Property_MapMatcher_Name());
        property.setDescription(Bundle.CLT_OSMEvaluatorProcess_Property_MapMatcher_Description());
        property.setValue(MapMatcher.Factory.getDefault().getClass().getName());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId(PROP_NAME_MAP_PROVIDER_INSTANCE);
        property.setJavaType(String.class.getName());
        property.setName(Bundle.CLT_OSMEvaluatorProcess_Property_MapProvider_Name());
        property.setDescription(Bundle.CLT_OSMEvaluatorProcess_Property_MapProvider_Description());
        property.setValue(MapProvider.Factory.getDefault().getClass().getName());
        propertySet.getProperties().add(property);

        PropertySection propertySection = new PropertySection();
        propertySection.setId(OSMEvaluatorProcess.class.getName());
        propertySection.setName("Properties");
        propertySection.setDescription("Properties of this Evaluator process.");
        propertySection.getPropertySet().add(propertySet);
        propertySection.getPropertySet();
        processDescriptor.getProperties().getSections().add(propertySection);

        return processDescriptor;
    }

    @Override
    public org.openide.nodes.Node getNodeDelegate() {
        if (node == null) {
            node = new OSMEvaluatorProcessNode(OSMEvaluatorProcess.this);
        }
        return node;
    }

    private static class OSMEvaluatorProcessNode extends AggregationProcessNode {

        private final OSMEvaluatorProcess process;

        public OSMEvaluatorProcessNode(OSMEvaluatorProcess process) {
            super(process);
            this.process = process;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = Sheet.createPropertiesSet();
            sheet.put(set);


            if (process != null && process.getProcessDescriptor() != null) {
                ProcessDescriptor processDescriptor = process.getProcessDescriptor();

                for (PropertySection section : processDescriptor.getProperties().getSections()) {
                    if (OSMEvaluatorProcess.class.getName().equals(section.getId())) {
                        for (de.fub.mapsforge.project.aggregator.xml.PropertySet propertySet : section.getPropertySet()) {
                            if (OSMEvaluatorProcess.class.getName().equals(propertySet.getId())) {
                                for (final de.fub.mapsforge.project.aggregator.xml.Property property : propertySet.getProperties()) {
                                    if (PROP_NAME_MAP_MATCH_INSTANCE.equals(property.getId())) {
                                        ClassProperty classProperty = new MapMatcherProperty(property);
                                        set.put(classProperty);
                                    } else if (PROP_NAME_MAP_PROVIDER_INSTANCE.equals(property.getId())) {
                                        ClassProperty classProperty = new MapProviderProperty(property);
                                        set.put(classProperty);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return sheet;
        }

        private class MapMatcherProperty extends ClassProperty {

            private final de.fub.mapsforge.project.aggregator.xml.Property property;
            private ClassWrapper wrapper = process.getMapMatcher() != null
                    ? new ClassWrapper(process.getMapMatcher().getClass())
                    : null;

            public MapMatcherProperty(de.fub.mapsforge.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), MapMatcher.class);
                this.property = property;
            }

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    throw new IllegalArgumentException("Null is not a valid value");
                } else if (wrapper == null || !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                    wrapper = val;
                    try {
                        MapMatcher matcher = MapMatcher.Factory.find(val.getQualifiedName());
                        if (matcher != null) {
                            process.setMapMatcher(matcher);
                            property.setValue(matcher.getClass().getName());
                        }
                    } catch (MapMatcher.MapMatcherNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        private class MapProviderProperty extends ClassProperty {

            private final de.fub.mapsforge.project.aggregator.xml.Property property;

            public MapProviderProperty(de.fub.mapsforge.project.aggregator.xml.Property property) {
                super(property.getId(), property.getName(), property.getDescription(), MapProvider.class);
                this.property = property;
            }
            private ClassWrapper wrapper = process.getMapProvider() != null
                    ? new ClassWrapper(process.getMapProvider().getClass())
                    : null;

            @Override
            public ClassWrapper getValue() throws IllegalAccessException, InvocationTargetException {
                return wrapper;
            }

            @Override
            public void setValue(ClassWrapper val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    throw new IllegalArgumentException("Null is not a valid Argument");
                } else if (wrapper != null && !val.getQualifiedName().equals(wrapper.getQualifiedName())) {
                    try {
                        wrapper = val;
                        MapProvider provider = MapProvider.Factory.find(val.getQualifiedName());
                        if (provider != null) {
                            process.setMapProvider(provider);
                            property.setValue(provider.getClass().getName());
                        }
                    } catch (MapProvider.MapProviderNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }
}
