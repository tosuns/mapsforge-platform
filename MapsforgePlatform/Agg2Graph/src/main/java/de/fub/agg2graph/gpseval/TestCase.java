package de.fub.agg2graph.gpseval;

import de.fub.agg2graph.gpseval.data.AggregatedData;
import de.fub.agg2graph.gpseval.data.DataLoader;
import de.fub.agg2graph.gpseval.data.filter.TrackFilter;
import de.fub.agg2graph.gpseval.data.filter.WaypointFilter;
import de.fub.agg2graph.gpseval.features.Feature;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A TestCase-instance is used to load GPS-data-files, run Weka with the loaded
 * data and return the results.
 * 
 * The GPS-data-files are loaded using a
 * {@link de.fub.agg2graph.gpseval.data.DataLoader DataLoader}-instance with
 * {@link de.fub.agg2graph.gpseval.data.filter.TrackFilter TrackFilter}s and
 * {@link de.fub.agg2graph.gpseval.data.filter.WaypointFilter WaypointFilter}s.
 * Weka is run using the {@link de.fub.agg2graph.gpseval.WekaEval WekaEval}
 * -class. All information needed for the test-case is loaded from a
 * {@link de.fub.agg2graph.gpseval.Config Config}-object. The results are stored
 * in a {@link de.fub.agg2graph.gpseval.WekaResult WekaResult}-object.
 * 
 */
public class TestCase {

	private Config mCfg;
	private Map<String, List<String>> mClassesFolderMapping;
	private List<Feature> mFeatures;
	private List<TrackFilter> mTrackFilters;
	private List<WaypointFilter> mWaypointFilters;

	/**
	 * Create a test-case based on a {@link de.fub.agg2graph.gpseval.Config
	 * Config}.
	 * 
	 * @param cfg
	 */
	public TestCase(Config cfg) {
		mCfg = cfg;
		mFeatures = mCfg.getFeatures();
		mClassesFolderMapping = mCfg.getClassesFolderMapping();
		mTrackFilters = mCfg.getTrackFilters();
		mWaypointFilters = mCfg.getWaypointFilters();
	}

	/**
	 * Run the test-case: Load GPS-data-files, run Weka and return the results.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws de.fub.agg2graph.gpseval.TestCase.NoDataException
	 * @throws Exception
	 */
	public TestResult run() throws FileNotFoundException, IOException,
			NoDataException, Exception {
		// 1. Load GPS-tracks
		DataLoader gpsLoader = new DataLoader();
		gpsLoader.addFeatures(mFeatures);
		gpsLoader.addTrackFilters(mTrackFilters);
		gpsLoader.addWaypointFilters(mWaypointFilters);
		Map<String, List<AggregatedData>> gpsData = gpsLoader
				.loadData(mClassesFolderMapping);

		boolean noData = true;
		for (String className : gpsData.keySet()) {
			if (!gpsData.get(className).isEmpty()) {
				noData = false;
				break;
			}
		}
		if (noData) {
			throw new NoDataException();
		}

		// 2. Run Weka
		WekaEval eval = new WekaEval(gpsData, mFeatures);
		eval.setTrainingSetSize(mCfg.getTrainingSetSize());
		eval.setCrossValidationFolds(mCfg.getCrossValidationFolds());
		eval.run();

		return new TestResult(mCfg, eval.getCrossValidationResults(),
				eval.getTrainingTestResults());
	}

	public class NoDataException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}
}
