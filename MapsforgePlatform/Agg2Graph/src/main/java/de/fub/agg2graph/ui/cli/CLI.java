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
package de.fub.agg2graph.ui.cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggregationStrategyFactory;
import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.tiling.CachingStrategyFactory;
import de.fub.agg2graph.agg.tiling.DefaultCachingStrategy;
import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.input.FileHandler;
import de.fub.agg2graph.input.GPSCleaner;
import de.fub.agg2graph.input.GPXReader;
import de.fub.agg2graph.input.ImportHistory;
import de.fub.agg2graph.management.Statistics;
import de.fub.agg2graph.osm.IExporter;
import de.fub.agg2graph.osm.OsmExporter;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.EditableObject;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.ui.DSL;
import de.fub.agg2graph.ui.StepStorage;
import de.fub.agg2graph.ui.Token;
import de.fub.agg2graph.ui.gui.CalcThread;

public class CLI {
	private static final Logger logger = Logger.getLogger("agg2graph.cli");

	protected List<Token> tokens;
	protected StepStorage stepStorage = new StepStorage();
	protected File outputFolder;
	protected File file;

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException {
		CLI cli = new CLI();
		cli.run(args);
		System.exit(0);
	}

	/**
	 * Execute a number of scripts using a simple domain-specific language
	 * without UI.
	 * 
	 * @param args
	 *            list of filenames (search is conducted in folder cli), if
	 *            empty all files in that folder are processed
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public void run(String[] args) throws FileNotFoundException, IOException,
			InterruptedException {
		Map<String, String> keyValueMap;
		File inputFolder;
		File aggFolder;

		stepStorage.setOpenOsmExportFile(false);

		// test all files or do we have filenames in args?
		File[] targetFiles = CLI.getTargetFiles(args, new File("test/cli"));

		for (File currentFile : targetFiles) {
			file = currentFile;
			if (file.isDirectory()) {
				continue;
			}
			logger.info("processing file " + file.getName());

			inputFolder = null;
			aggFolder = null;
			String line = null;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			// parse lines
			while ((line = bufferedReader.readLine()) != null) {
				logger.fine("parsing line \"" + line + "\"");
				tokens = DSL.getTokens(line);
				keyValueMap = DSL.getKeyValueMap(tokens);
				if (tokens.size() < 1) {
					continue;
				}

				// loop tokens
				Token t;
				int tokenIndex = 0;
				t = tokens.get(tokenIndex);
				// handle steps that are available in the GUI as well
				if ("input".equals(t.name)) {
					inputFolder = new File("test/input/"
							+ tokens.get(tokenIndex + 1));
					aggFolder = new File("test/agg/"
							+ tokens.get(tokenIndex + 1));
				} else if ("clean".equals(t.name)) {
					stepStorage
							.setCleaningRamerDouglasPeuckerFilter(new RamerDouglasPeuckerFilter(
									0));
					stepStorage.setGpsCleaner(new GPSCleaner());
					parseParameters(t.name, keyValueMap);
				} else if ("agg".equals(t.name)) {
					// process input data track by track using the
					// infrastructure setup
					if (inputFolder == null) {
						continue;
					}
					aggFolder = (tokens.size() > tokenIndex + 1 ? new File(
							"test/agg/" + tokens.get(tokenIndex + 1).name)
							: aggFolder);
					aggFolder.mkdirs();
					ImportHistory importHistory = new ImportHistory(aggFolder);

					makeAgg(aggFolder);
					parseParameters(t.name, keyValueMap);

					File[] inputFiles = inputFolder
							.listFiles(FileHandler.gpxFilter);
					Arrays.sort(inputFiles);
					int counter = 0;
					for (File inputFile : inputFiles) {
						logger.info("processing file "
								+ inputFile.getAbsolutePath());
						if (!importHistory.needsImport(inputFile)) {
							continue;
						}

						// read gpx file
						List<GPSSegment> segments = GPXReader
								.getSegments(inputFile);
						if (segments == null) {
							logger.severe("Bad file: " + file);
							continue;
						}
						List<GPSSegment> cleanSegments = new ArrayList<GPSSegment>(
								0);
						for (GPSSegment segment : segments) {
							segment.addIDs("I" + counter++);
							// clean?
							if (stepStorage
									.getCleaningRamerDouglasPeuckerFilter() != null
									|| stepStorage.getGpsCleaner() != null) {
								GPSCleaner gpsCleaner = stepStorage
										.getGpsCleaner();
								RamerDouglasPeuckerFilter rdpf = stepStorage
										.getCleaningRamerDouglasPeuckerFilter();

								cleanSegments = gpsCleaner.clean(segment);
								for (GPSSegment cleanSegment : cleanSegments) {
									// run through Douglas-Peucker here
									// (slightly modified
									// perhaps to avoid too long edges)
									cleanSegment = rdpf.simplify(cleanSegment);
								}
							} else {
								// cleaning is optional
								cleanSegments.add(segment);
							}
						}
						// aggregate
						for (GPSSegment cleanSegment : cleanSegments) {
							logger.info(String.format("adding segment no. %d",
									++counter));
							stepStorage.getAggContainer().addSegment(
									cleanSegment);
						}
						importHistory.wasImported(inputFile);
					}
					importHistory.close();
				} else if ("road".equals(t.name)) {
					RoadNetwork roadNetwork = new RoadNetwork();
					stepStorage.setRoadNetwork(roadNetwork);
					parseParameters(t.name, keyValueMap);
					roadNetwork.parse(stepStorage.getAggContainer(),
							stepStorage);
				} else if ("osm".equals(t.name)) {
					IExporter osmExport = new OsmExporter();
					stepStorage.setExporter(osmExport);
					parseParameters(t.name, keyValueMap);
					String filename = tokens.size() > tokenIndex + 1 ? tokens
							.get(tokenIndex + 1).name : "osm-out.xml";
					File outFile = new File(filename);
					logger.info("Writing OSM data to " + outFile);
					osmExport.setTargetFile(outFile);
					osmExport.export(stepStorage.getRoadNetwork());
				} else if ("loadagg".equals(t.name)) {
					aggFolder = (tokens.size() > tokenIndex + 1 ? new File(
							"test/agg/" + tokens.get(tokenIndex + 1).name)
							: aggFolder);
					makeAgg(aggFolder);
				} else if ("saveagg".equals(t.name)) {
					aggFolder = (tokens.size() > tokenIndex + 1 ? new File(
							"test/agg/" + tokens.get(tokenIndex + 1).name)
							: aggFolder);
					stepStorage.getAggContainer().save();
				} else if ("clearagg".equals(t.name)) {
					aggFolder = (tokens.size() > tokenIndex + 1 ? new File(
							"test/agg/" + tokens.get(tokenIndex + 1).name)
							: aggFolder);
					stepStorage.getAggContainer().clear();
					stepStorage.setAggContainer(null);
					FileHandler.removeDirectory(aggFolder);
				} else if ("statistics".equals(t.name)) {
					printStatistics("statistics.txt");
				}
			}
			bufferedReader.close();
		}
	}

	private void makeAgg(File aggFolder) {
		if (stepStorage.getAggContainer() != null
				&& stepStorage.getAggContainer().getDataSource()
						.equals(aggFolder)) {
			return;
		}
		IAggregationStrategy aggStrat = AggregationStrategyFactory.getObject();
		ICachingStrategy cacheStrat = CachingStrategyFactory.getObject();
		AggContainer container = AggContainer.createContainer(aggFolder,
				aggStrat, cacheStrat);
		cacheStrat.clear();
		stepStorage.setAggContainer(container);
	}

	public static File[] getTargetFiles(String[] args, File baseFolder) {
		File[] targetFiles;
		if (args.length > 0) {
			targetFiles = new File[args.length];
			for (int i = 0; i < args.length; i++) {
				targetFiles[i] = new File(baseFolder.getAbsolutePath()
						+ File.separator + args[i]);
			}
		} else {
			File sourceFolder = new File(baseFolder.getAbsolutePath());
			targetFiles = sourceFolder.listFiles();
		}
		return targetFiles;
	}

	public void parseParameters(String tokenName,
			Map<String, String> parameterMap) {
		List<ClassObjectEditor> objectEditors = stepStorage
				.getObjectEditorsForLevel(tokenName);
		for (String key : parameterMap.keySet()) {
			for (ClassObjectEditor objectEditor : objectEditors) {
				objectEditor.setValue(key, parameterMap.get(key));
			}
		}
	}

	public static void printParameters() {
		CLI cli = new CLI();

		// init a default set
		StepStorage stepStorage = new StepStorage();
		IAggregationStrategy aggStrat = AggregationStrategyFactory.getObject();
		DefaultCachingStrategy cacheStrat = new DefaultCachingStrategy();
		AggContainer agg = AggContainer.createContainer(null, aggStrat,
				cacheStrat);
		cacheStrat.clear();
		stepStorage.setAggContainer(agg);

		stepStorage
				.setCleaningRamerDouglasPeuckerFilter(new RamerDouglasPeuckerFilter(
						0));
		stepStorage.setGpsCleaner(new GPSCleaner());

		cli.makeAgg(null);

		RoadNetwork roadNetwork = new RoadNetwork();
		stepStorage.setRoadNetwork(roadNetwork);

		IExporter osmExport = new OsmExporter();
		stepStorage.setExporter(osmExport);

		// get parameter string
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < CalcThread.stepNames.length; i++) {
			sb.append("Parameters for " + CalcThread.stepNames[i]).append("\n");
			List<ClassObjectEditor> objects = stepStorage
					.getObjectEditorsForLevel(i);
			for (ClassObjectEditor classObjectEditor : objects) {
				sb.append(
						String.format("\t%s (%s)", classObjectEditor
								.getObject().getClass().getSimpleName(),
								classObjectEditor.getDescription())).append(
						"\n");
				for (EditableObject o : classObjectEditor.getEditableObjects()) {
					sb.append(
							String.format("\t\t%s: %s: %s", o.name,
									o.getTypeString(), o.value.toString()))
							.append("\n");
				}
			}
		}
		// output
		System.out.println(sb.toString());
		try {
			BufferedWriter out = new BufferedWriter(
					new FileWriter("parameters"));
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
		}
	}

	public void printStatistics(String baseFilename) throws IOException {
		File outputFile = new File(outputFolder.getAbsoluteFile()
				+ File.separator + file.getName() + "-"
				+ baseFilename.split("\\.")[0]
				+ (tokens.size() > 1 ? "-" + tokens.get(1).name : "") + "."
				+ baseFilename.split("\\.")[1]);

		BufferedWriter outputStream = new BufferedWriter(new FileWriter(
				outputFile));
		Map<String, Double> stats = Statistics.getData(stepStorage
				.getRoadNetwork());
		String outputString;
		for (String stat : stats.keySet()) {
			outputString = stat + ": " + stats.get(stat);
			System.out.println(outputString);
			outputStream.write(outputString);
			outputStream.write("\n");
		}
		outputStream.close();
	}
}
