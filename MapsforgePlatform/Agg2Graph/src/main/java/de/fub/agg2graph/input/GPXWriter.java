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
package de.fub.agg2graph.input;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Save {@link GPSSegment} data to a gpx file.
 *
 * @author Johannes Mitlmeier
 *
 */
public class GPXWriter {

    private static final String fileTemplate = "<?xml version=\"1.0\"?>\n<gpx version=\"1.1\" creator=\"GPXWriter\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n<trk><trkseg>\n%s</trkseg></trk></gpx>";
    private static final String fileTemplateMoreSegments = "<?xml version=\"1.0\"?>\n<gpx version=\"1.1\" creator=\"GPXWriter\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n<trk>\n%s</trk></gpx>";

    private static final String segmentTemplate = "<trkseg>\n%s</trkseg>";
    private static final String pointTemplate = "<trkpt lat=\"%s\" lon=\"%s\" />";

    public static void writeSegment(File targetFile, GPSSegment segment)
            throws IOException {
        StringBuilder sb = new StringBuilder();

        for (GPSPoint point : segment) {
            if (point.getK() < 1) {
                sb.append(
                        String.format(pointTemplate, point.getLat(), point.getLon(), 1))
                        .append("\n");

            } else {
                sb.append(
                        String.format(pointTemplate, point.getLat(), point.getLon(), 1))
                        .append("\n");
            }

        }
        FileWriter fstream = new FileWriter(targetFile);
        fstream.write(String.format(fileTemplate, sb.toString()));
        fstream.close();
    }

    public static void writeSegments(File targetFile, List<GPSSegment> segments)
            throws IOException {
        StringBuilder segmentBuilder = new StringBuilder();

        for (GPSSegment segment : segments) {
            StringBuilder pointBuilder = new StringBuilder();
            for (GPSPoint point : segment) {
                if (point.getK() < 1) {
                    pointBuilder.append(
                            String.format(pointTemplate, point.getLat(), point.getLon(), 1))
                            .append("\n");
                } else {
                    pointBuilder.append(
                            String.format(pointTemplate, point.getLat(), point.getLon(), 1))
                            .append("\n");
                }
            }
            segmentBuilder.append(String.format(segmentTemplate, pointBuilder.toString())).
                    append("\n");
        }

        FileWriter fstream = new FileWriter(targetFile);
        fstream.write(String.format(fileTemplateMoreSegments, segmentBuilder.toString()));
        fstream.close();
    }

}
