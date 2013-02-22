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
package de.fub.agg2graph.input;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.GPSTrack;

/**
 * Extract data structures ({@link GPSTrack}s, {@link GPSSegment}s,
 * {@link GPSPoint}s) from a gpx file.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class GPXPointReaderHandler extends DefaultHandler {
	private static Logger logger = Logger.getLogger("agg2graph");

	private List<GPSTrack> tracks;
	private List<GPSSegment> segments;
	private List<GPSPoint> points;

	public GPXPointReaderHandler() {
		this.tracks = new ArrayList<GPSTrack>();
		this.segments = new ArrayList<GPSSegment>();
		this.points = new ArrayList<GPSPoint>();
	}

	@Override
	public void startDocument() {
		logger.finer("startDocument");
	}

	@Override
	public void endDocument() {
		logger.finer("endDocument");
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) {
		if ("trk".equals(qName)) {
			tracks.add(new GPSTrack());
		}
		if ("trkseg".equals(qName)) {
			GPSSegment segment = new GPSSegment();
			if (!tracks.isEmpty()) {
				tracks.get(tracks.size() - 1).add(segment);
			}
			segments.add(segment);
		} else if ("trkpt".equals(qName)) {
			GPSPoint point = new GPSPoint();
			for (int i = 0; i < atts.getLength(); i++) {
				if ("lon".equals(atts.getQName(i))) {
					point.setLon(Float.parseFloat(atts.getValue(i)));
				} else if ("lat".equals(atts.getQName(i))) {
					point.setLat(Float.parseFloat(atts.getValue(i)));
				}
			}
			points.add(point);
			if (!segments.isEmpty()) {
				segments.get(segments.size() - 1).add(point);
			}
		}

	}

	public List<GPSPoint> getPoints() {
		return points;
	}

	public List<GPSSegment> getSegments() {
		return segments;
	}

	public List<GPSTrack> getTracks() {
		return tracks;
	}

}
