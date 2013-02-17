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
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * Provide access to the structures stored in a gpx file.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class GPXReader {

	public static List<GPSTrack> getTracks(File sourceFile) {
		GPXPointReaderHandler handler = process(sourceFile);
		if (handler == null) {
			return null;
		}
		return handler.getTracks();
	}

	public static List<GPSSegment> getSegments(File sourceFile) {
		GPXPointReaderHandler handler = process(sourceFile);
		if (handler == null) {
			return null;
		}
		return handler.getSegments();
	}

	private static GPXPointReaderHandler process(File sourceFile) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			GPXPointReaderHandler handler = new GPXPointReaderHandler();
			saxParser.parse(sourceFile, handler);
			return handler;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<GPSPoint> getOrderedPoints(File sourceFile)
			throws ParserConfigurationException, SAXException, IOException {
		Comparator<GPSPoint> comparator = new Comparator<GPSPoint>() {

			@Override
			public int compare(GPSPoint o1, GPSPoint o2) {
				return (int) Math.signum(o1.getLon() - o2.getLon());
			}
		};

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		GPXPointReaderHandler handler = new GPXPointReaderHandler();
		saxParser.parse(sourceFile, handler);
		// sort
		List<GPSPoint> result = handler.getPoints();
		Collections.sort(result, comparator);
		return result;
	}

}
