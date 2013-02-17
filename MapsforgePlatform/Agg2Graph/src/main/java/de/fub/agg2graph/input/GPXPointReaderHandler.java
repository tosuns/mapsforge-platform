/*******************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Affero Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/agpl-3.0.html
 * 
 * Contributors:
 *     Johannes Mitlmeier - initial API and implementation
 ******************************************************************************/
package de.fub.agg2graph.input;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.GPSTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

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
