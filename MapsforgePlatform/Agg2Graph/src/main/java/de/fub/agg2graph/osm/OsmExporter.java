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
package de.fub.agg2graph.osm;

import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.roadgen.Road;
import de.fub.agg2graph.roadgen.Road.RoadType;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.ILocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Export a {@link RoadNetwork} to an xml file as accepted by OpenStreetMap.
 *
 * @author Johannes Mitlmeier
 *
 */
public class OsmExporter implements IExporter {

    private static final Logger LOG = Logger.getLogger(OsmExporter.class.getName());
    public String user = "agg2graph";
    public String uid = "1";
    public int osmNodeStartId = 1;
    public int osmWayStartId = 1;
    private String dateString;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /*
     * XML templates, see
     * http://wiki.openstreetmap.org/wiki/OSM_XML#OSM_XML_file_format
     */
    // TODO calculate bounds (or take them from somewhere)
    private static final String XML_FILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<osm version=\"0.6\" generator=\"agg2graph\">\n"
            + "<bounds minlat=\"%s\" minlon=\"%s\" maxlat=\"%s\" maxlon=\"%s\"/>\n%s</osm>";
    /* http://wiki.openstreetmap.org/wiki/Node */
    private static final String XML_NODE_PATTERN = "<node id=\"%d\" lat=\"%.7f\" lon=\"%.7f\" version=\"1\" changeset=\"1\" user=\"%s\" uid=\"%s\" visible=\"true\" timestamp=\"%s\"></node>\n";
    // timestamp format: 2007-01-28T11:40:26Z
	/* http://wiki.openstreetmap.org/wiki/Way */
    private static final String XML_WAY_PATTERN = "<way id=\"%s\" highway=\"%s\" %s visible=\"true\" timestamp=\"%s\" version=\"1\" changeset=\"1\" user=\"%s\" uid=\"%s\">\n%s</way>\n";
    private static final String XML_WAY_NODE_REF = "<nd ref=\"%s\"/>\n";
    public final RamerDouglasPeuckerFilter RDPF = new RamerDouglasPeuckerFilter(10);

    @Override
    public void export(RoadNetwork roadNetwork, OutputStream outputStream) {
        String content = getXmlData(roadNetwork);

        // write to file
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
            out.write(content);
            out.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Get a string representation of the data for exporting to a xml file.
     *
     * @param roadNetwork
     * @return
     */
    private String getXmlData(RoadNetwork roadNetwork) {
        StringBuilder sb = new StringBuilder();
        int currentNodeID = osmNodeStartId;
        int currentWayID = osmWayStartId;
        dateString = dateFormatter.format(new Date());
        ArrayList<Way> ways = new ArrayList<>();

        // generate output
        for (Road road : roadNetwork.getRoads()) {
            if (road.isVisible()) {
                // write nodes as OSM nodes
                List<? extends ILocation> simplifiedPath = road.getNodes(); //RDPF.simplify(road.getPath());
                if (!simplifiedPath.isEmpty()) {

                    Way way = new Way(String.valueOf(currentWayID++));
                    way.setRoadType(road.getType());
                    way.setOneWay(road.isOneWay());
                    ways.add(way);

                    for (ILocation node : simplifiedPath) {
                        sb.append(getNodeString(node, currentNodeID));
                        way.getNds().add(new Nd(String.valueOf(currentNodeID++)));
                    }
                }
            }
        }

        for (Way way : ways) {
            sb.append(getWayString(way));
        }

        String xmlContent = String.format(XML_FILE, 0, 0, 0, 0, sb.toString());
        return xmlContent;
    }

    private String getWayString(Way way) {
        StringBuilder sb = new StringBuilder();
        ILocation node;
        for (Nd nd : way.getNds()) {
            sb.append(String.format(XML_WAY_NODE_REF, nd.getRef()));
        }

        String type = "unknown";
        switch (way.getRoadType()) {
            case PRIMARY:
                type = "primary";
                break;
            case SECONDARY:
                type = "secondary";
                break;
            case TERTIARY:
                type = "tertiary";
                break;
        }

        return String.format(Locale.ENGLISH, XML_WAY_PATTERN, way.getId(), type,
                way.isOneWay() ? "oneway=\"yes\"" : "", dateString, user, uid,
                sb.toString());
    }

    private String getNodeString(ILocation loc, int currentNodeID) {
        return String.format(Locale.ENGLISH, XML_NODE_PATTERN, currentNodeID,
                loc.getLat(), loc.getLon(), user, uid, dateString);
    }

    @Override
    public List<ClassObjectEditor> getSettings() {
        ArrayList<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>(3);
        result.add(new ClassObjectEditor(this, Arrays.asList(new String[]{"rdpf", "targetFile"})));
        result.add(new ClassObjectEditor(this.RDPF));
        return result;
    }

    private static class Nd {

        private String ref = null;

        public Nd(String nodeRef) {
            this.ref = nodeRef;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }

    private static class Way {

        private ArrayList<Nd> nds = new ArrayList<>();
        private String id;
        private Road.RoadType roadType = RoadType.UNKNOWN;
        private boolean oneWay = true;

        public Way(String id) {
            this.id = id;
        }

        public ArrayList<Nd> getNds() {
            return nds;
        }

        public boolean isOneWay() {
            return oneWay;
        }

        public void setOneWay(boolean oneWay) {
            this.oneWay = oneWay;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public RoadType getRoadType() {
            return roadType;
        }

        public void setRoadType(RoadType roadType) {
            this.roadType = roadType;
        }
    }
}
