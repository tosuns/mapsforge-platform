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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.GPSTrack;

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
