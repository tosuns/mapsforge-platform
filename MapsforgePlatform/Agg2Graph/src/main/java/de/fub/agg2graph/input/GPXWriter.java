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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Save {@link GPSSegment} data to a gpx file.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class GPXWriter {
	private static final String fileTemplate = "<?xml version=\"1.0\"?>\n<gpx version=\"1.1\" creator=\"GPXWriter\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n<trk><trkseg>\n%s</trkseg></trk></gpx>";
	private static final String pointTemplate = "<trkpt lat=\"%s\" lon=\"%s\" />";

	public static void writeSegment(File targetFile, GPSSegment segment)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		for (GPSPoint point : segment) {
			sb.append(
					String.format(pointTemplate, point.getLat(), point.getLon()))
					.append("\n");
		}
		FileWriter fstream = new FileWriter(targetFile);
		fstream.write(String.format(fileTemplate, sb.toString()));
		fstream.close();
	}
}
