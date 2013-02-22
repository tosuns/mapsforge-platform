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
package de.fub.agg2graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.input.FileHandler;
import de.fub.agg2graph.input.GPXReader;
import de.fub.agg2graph.structs.GPSSegment;

public class RDPFTester {

	/**
	 * Class for outputting evaluation of the efficacy of a Ramer Douglas
	 * Peucker filter on a real dataset.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		File inputFolder = new File("test/input/bevern");
		RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(0);

		StringBuilder sb = new StringBuilder();
		sb.append("x   y\n");
		for (int eps = 0; eps < 20; eps++) {
			double sumNodesBefore = 0;
			double sumNodesAfter = 0;

			rdpf.setEpsilon(eps);

			File[] inputFiles = inputFolder.listFiles(FileHandler.gpxFilter);
			Arrays.sort(inputFiles);
			for (File inputFile : inputFiles) {
				List<GPSSegment> segments = GPXReader.getSegments(inputFile);
				if (segments == null) {
					continue;
				}
				for (GPSSegment segment : segments) {
					sumNodesBefore += segment.size();
					sumNodesAfter += rdpf.simplify(segment).size();
				}
			}
			sb.append(String.format(Locale.ENGLISH, "%d   %.3f", eps,
					(sumNodesAfter / sumNodesBefore) * 100));
			sb.append("\n");
		}

		// output
		System.out.println(sb.toString());
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("rdpf.data"));
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
		}
	}
}
