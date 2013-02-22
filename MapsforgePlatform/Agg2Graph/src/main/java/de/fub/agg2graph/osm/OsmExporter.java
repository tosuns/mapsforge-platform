/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
package de.fub.agg2graph.osm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.roadgen.Road;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.ILocation;

/**
 * Export a {@link RoadNetwork} to an xml file as accepted by OpenStreetMap.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class OsmExporter implements IExporter {
	public String user = "agg2graph";
	public String uid = "1";
	public int osmNodeStartId = 1;
	public int osmWayStartId = 1;

	private int currentNodeID;
	private int currentWayID;
	private String dateString;
	private Map<String, Integer> locToID = new HashMap<String, Integer>();
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	private File targetFile = null;

	/*
	 * XML templates, see
	 * http://wiki.openstreetmap.org/wiki/OSM_XML#OSM_XML_file_format
	 */
	// TODO calculate bounds (or take them from somewhere)
	private String xmlFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<osm version=\"0.6\" generator=\"agg2graph\">\n"
			+ "<bounds minlat=\"%s\" minlon=\"%s\" maxlat=\"%s\" maxlon=\"%s\"/>\n%s</osm>";
	/* http://wiki.openstreetmap.org/wiki/Node */
	private String xmlNode = "<node id=\"%d\" lat=\"%.7f\" lon=\"%.7f\" version=\"1\" changeset=\"1\" user=\"%s\" uid=\"%s\" visible=\"true\" timestamp=\"%s\"></node>\n";
	// timestamp format: 2007-01-28T11:40:26Z
	/* http://wiki.openstreetmap.org/wiki/Way */
	private String xmlWay = "<way id=\"%d\" highway=\"%s\" %s visible=\"true\" timestamp=\"%s\" version=\"1\" changeset=\"1\" user=\"%s\" uid=\"%s\">\n%s</way>\n";
	private String xmlWayNodeRef = "<nd ref=\"%d\"/>\n";
	public RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(10);

	@Override
	public void export(RoadNetwork roadNetwork) {
		String content = getXmlData(roadNetwork);

		// write to file
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));
			out.write(content);
			out.close();
		} catch (IOException e) {
			System.out.println("Could not write file " + targetFile);
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
		currentNodeID = osmNodeStartId;
		currentWayID = osmWayStartId;
		dateString = dateFormatter.format(new Date());

		// generate output
		for (Road r : roadNetwork.roads) {
			if (!r.isVisible()) {
				continue;
			}
			// write nodes as OSM nodes
			for (ILocation loc : rdpf.simplify(r.path)) {
				sb.append(getNodeString(loc));
				locToID.put(loc.getID(), currentNodeID++);
			}
		}
		for (Road r : roadNetwork.roads) {
			if (!r.isVisible()) {
				continue;
			}
			// write roads as nodes and connections
			sb.append(getWayString(r));
			currentWayID++;
		}

		String xmlContent = String.format(xmlFile, 0, 0, 0, 0, sb.toString());
		return xmlContent;
	}

	private String getWayString(Road r) {
		StringBuilder sb = new StringBuilder();
		ILocation node;
		List<? extends ILocation> nodes = rdpf.simplify(r.path);
		for (int i = 0; i < nodes.size(); i++) {
			// System.out.println(locToID);
			node = nodes.get(i);
			sb.append(String.format(xmlWayNodeRef, locToID.get(node.getID())));
		}
		String type = "unknown";
		switch (r.getType()) {
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
		return String.format(Locale.ENGLISH, xmlWay, currentWayID, type,
				r.isOneWay() ? "oneway=\"yes\"" : "", dateString, user, uid,
				sb.toString());
	}

	private String getNodeString(ILocation loc) {
		return String.format(Locale.ENGLISH, xmlNode, currentNodeID,
				loc.getLat(), loc.getLon(), user, uid, dateString);
	}

	@Override
	public File getTargetFile() {
		return targetFile;
	}

	@Override
	public void setTargetFile(File targetFile) {
		this.targetFile = targetFile;
	}

	@Override
	public List<ClassObjectEditor> getSettings() {
		ArrayList<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>(
				3);
		result.add(new ClassObjectEditor(this, Arrays.asList(new String[] {
				"rdpf", "targetFile" })));
		result.add(new ClassObjectEditor(this.rdpf));
		return result;
	}
}
