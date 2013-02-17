package de.fub.agg2graph.gpseval.data;

import de.fub.agg2graph.gpseval.data.file.TrackFile;
import de.fub.agg2graph.gpseval.data.file.TrackFileFactory;
import de.fub.agg2graph.gpseval.data.filter.TrackFilter;
import de.fub.agg2graph.gpseval.data.filter.WaypointFilter;
import de.fub.agg2graph.gpseval.features.Feature;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A DataLoader is used to load GPS-data stored in files.
 *
 * <p>For each GPS-data-file a TrackFile-instance is created using
 * TrackFileFactory. This allows one to read GPS-data from files of different
 * formats (e.g. csv, gpx, ...) based on file-extension. To support a new
 * extension you can register your own TrackFile-class on the
 * TrackFileFactory.</p>
 *
 * <p>When loading GPS-data-files, TrackFilters and WaypointFilters are applied,
 * which you need to add (before loading data) using the methods addTrackFilters
 * and addWaypointFilters.</p>
 *
 * <p>Moreover you need to specify the feature set to use by using the methods
 * {@link de.fub.agg2graph.gpseval.data.DataLoader#addFeature(Feature feature) addFeature}
 * and {@link de.fub.agg2graph.gpseval.data.DataLoader#addFeatures(List) addFeatures}
 * before loading data.</p>
 */
public class DataLoader {

    private TrackFileFactory mGPSDataFileFactory = TrackFileFactory.getFactory();
    private List<Feature> mFeatures = new ArrayList<>();
    private List<TrackFilter> mTrackFilters = new ArrayList<>();
    private List<WaypointFilter> mWaypointFilters = new ArrayList<>();

    /**
     * Load all GPS-data-files for each folder of a class.
     *
     * @param classesFolderMapping The mapping from class-names to folders,
     * which contain the GPS-data-files of the respective class.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Map<String, List<AggregatedData>> loadData(Map<String, List<String>> classesFolderMapping) throws FileNotFoundException, IOException {
        Map<String, List<AggregatedData>> gpsData = new HashMap<>();

        for (String className : classesFolderMapping.keySet()) {
            List<String> dataFolders = classesFolderMapping.get(className);
            List<AggregatedData> classGpsData = loadDataFolders(className, dataFolders);
            gpsData.put(className, classGpsData);
        }

        return gpsData;
    }

    /**
     * Load all GPS-data-files of a folder which contains data for the specified
     * class.
     *
     * @param className The name of the class to which the data inside the
     * dataFolders belong to.
     * @param dataFolders
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<AggregatedData> loadDataFolders(String className, List<String> dataFolders) throws FileNotFoundException, IOException {
        List<AggregatedData> gpsData = new ArrayList<>();
        for (String dataFolder : dataFolders) {
            List<AggregatedData> folderGpsData = loadDataFolder(className, dataFolder);
            gpsData.addAll(folderGpsData);
        }
        return gpsData;
    }

    /**
     * Load all GPS-data-files of a single folder which contains data for the
     * specified class.
     *
     * @param className The name of the class to which the data inside the
     * dataFolders belong to.
     * @param dataFolder
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<AggregatedData> loadDataFolder(String className, String dataFolder) throws FileNotFoundException, IOException {
        List<AggregatedData> gpsData = new ArrayList<>();

        Path dataPath = Paths.get(dataFolder);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataPath)) {
            for (Path entry : stream) {
                if (!Files.isDirectory(entry)) {
                    TrackFile gpsFile = mGPSDataFileFactory.newGPSDataFile(entry);

                    boolean passedAllFilter = true;
                    for (TrackFilter filter : mTrackFilters) {
                        if (!filter.filter(gpsFile, className)) {
                            passedAllFilter = false;
                            break;
                        }
                    }

                    if (passedAllFilter) {

                        AggregatedData data = loadDataFile(gpsFile);
                        if (data != null) {
                            gpsData.add(data);
                        }

                    }
                }
            }
        }

        return gpsData;
    }

    /**
     * Load a single GPS-data-file and return the aggregated data.
     *
     * @param gpsFile The GPS-data-file to load.
     * @return The aggregated data or null if file could not be loaded.
     */
    public AggregatedData loadDataFile(TrackFile gpsFile) {
        if (gpsFile == null) {
            Logger.getLogger(DataLoader.class.getName()).log(Level.WARNING, "No suitable GPSDataFile-class found!");
            return null;
        }

        // reset filters and add to file
        decorateWithWaypointFilters(gpsFile);

        for (Feature feature : mFeatures) {
            feature.reset();
        }

        for (Waypoint gpsData : gpsFile) {

            for (Feature feature : mFeatures) {
                feature.addWaypoint(gpsData);
            }

        }

        AggregatedData data = new AggregatedData();

        for (Feature feature : mFeatures) {
            data.addData(feature);
        }

        return data;
    }

    /**
     * Add a feature to the feature set.
     *
     * @param feature
     */
    public void addFeature(Feature feature) {
        mFeatures.add(feature);
    }

    /**
     * Add the given features to the feature set.
     *
     * @param features
     */
    public void addFeatures(List<Feature> features) {
        mFeatures.addAll(features);
    }

    /**
     * Add a track filter to the list of track filters.
     *
     * @param trackFilters
     */
    public void addTrackFilters(List<TrackFilter> trackFilters) {
        mTrackFilters.addAll(trackFilters);
    }

    /**
     * Add a waypoint filter to the list of waypoint filters.
     *
     * @param waypointFilters
     */
    public void addWaypointFilters(List<WaypointFilter> waypointFilters) {
        mWaypointFilters.addAll(waypointFilters);
    }

    /**
     * Decorate the given GPS-data-file with each waypoint filter.
     *
     * @param gpsFile
     */
    private void decorateWithWaypointFilters(TrackFile gpsFile) {
        for (WaypointFilter waypointFilter : mWaypointFilters) {
            waypointFilter.reset();
            gpsFile.addWaypointFilter(waypointFilter);
        }
    }
}
