package de.fub.agg2graph.gpseval;

import de.fub.agg2graph.gpseval.TestCase.NoDataException;
import de.fub.agg2graph.gpseval.output.DetailedHTMLOutput;
import de.fub.agg2graph.gpseval.output.TestResultsOutput;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.DocumentException;

/**
 * This class contains the main entry point for the program.
 * 
 * <p>
 * Usage: java -cp &lt;classpath&gt; de.fub.agg2graph.gpseval.Main
 * &lt;config-folder&gt; &lt;results-folder&gt;
 * </p>
 * 
 * <p>
 * The config-folder should contain one or many configuration-files (xml-files).
 * Each config-file describes a test-case. For example it defines where
 * GPS-data-files can be found and to which class they belong to. Moreover it
 * defines features that should be included in the feature set. You can set
 * paramters, e.g. to determine which filters should be used to limit the used
 * data (based on tracks and GPS-waypoints).
 * </p>
 * 
 * @see de.fub.agg2graph.gpseval.ConfigFile
 */
public class Main {

	/**
	 * The main entry point. The
	 * {@link de.fub.agg2graph.gpseval.Main#start(String[] args) start}-method
	 * will be called.
	 * 
	 * @see de.fub.agg2graph.gpseval.Main.start
	 * 
	 * @param args
	 *            the command line arguments
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		Main app = new Main();
		app.start(args);
	}

	/**
	 * Read all config-files from the specified folder and run a
	 * {@link de.fub.agg2graph.gpseval.TestCase TestCase} for each of them. Then
	 * output the results using
	 * {@link de.fub.agg2graph.gpseval.output.DetailedHTMLOutput
	 * DetailedHTMLOutput}.
	 * 
	 * @see de.fub.agg2graph.gpseval.ConfigFile
	 * @see de.fub.agg2graph.gpseval.TestCase
	 * 
	 * @param args
	 *            The 0th-element must contain the path to the folder containing
	 *            the config-files. The 1st-element must contain the path to the
	 *            folder where the results should be stored.
	 * 
	 * @throws IOException
	 */
	public void start(String[] args) throws IOException {
		if (args.length < 2) {
			System.out
					.println("Usage: java -cp <classpath> de.fub.agg2graph.gpseval.Main <config-folder> <results-folder>");
			return;
		}

		Path configFolder = Paths.get(args[0]);
		Path resultsFolder = Paths.get(args[1]);

		if (!Files.exists(configFolder) || !Files.isDirectory(configFolder)) {
			System.out.println("<config-folder> not found!");
			return;
		}

		if (!Files.exists(resultsFolder) || !Files.isDirectory(resultsFolder)) {
			System.out.println("<results-folder> not found!");
			return;
		}

		List<String> configs = new LinkedList<>();
		List<TestResult> results = new LinkedList<>();

		// For each entry inside the config-folder
		try (DirectoryStream<Path> stream = Files
				.newDirectoryStream(configFolder)) {
			for (Path entry : stream) {
				if (!Files.isDirectory(entry)) {
					// entry is a file (should be a config-file)
					configs.add(entry.toString());
				}
			}
		}

		Collections.sort(configs);

		int i = 0;
		for (String config : configs) {
			System.out.println("[" + ++i + "/" + configs.size()
					+ "] Evaluating test case ...");

			Path entry = Paths.get(config);
			Config cfg = null;
			try {
				cfg = new ConfigFile(entry);
			} catch (DocumentException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null,
						entry);
			}

			if (cfg.getFeatures().isEmpty()) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
						"No features found ({0})!", entry);
				return;
			}

			// Create and run test-case
			TestCase test = new TestCase(cfg);
			try {
				TestResult result = test.run();
				results.add(result);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
						"Failed to run test-case!", ex);
			} catch (IOException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
						"Failed to run test-case!", ex);
			} catch (NoDataException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
						"No data found ({0})!", entry);
			} catch (Exception ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
						"Weka-Exception!", ex);
			}
		}

		// output results
		TestResultsOutput[] outputs = new TestResultsOutput[] { new DetailedHTMLOutput()
				.init(results, resultsFolder) };
		for (TestResultsOutput output : outputs) {
			output.run();
		}
	}
}
