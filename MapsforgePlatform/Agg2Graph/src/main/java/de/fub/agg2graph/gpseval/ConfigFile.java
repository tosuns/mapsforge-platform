package de.fub.agg2graph.gpseval;

import de.fub.agg2graph.gpseval.data.filter.TrackFilter;
import de.fub.agg2graph.gpseval.data.filter.TrackFilterFactory;
import de.fub.agg2graph.gpseval.data.filter.WaypointFilter;
import de.fub.agg2graph.gpseval.data.filter.WaypointFilterFactory;
import de.fub.agg2graph.gpseval.features.Feature;
import de.fub.agg2graph.gpseval.features.FeatureFactory;
import de.fub.agg2graph.gpseval.utils.Parameterizable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * This Config-class reads all information for a test-case from a xml-file.
 *
 * <p>This is a simple example-config:</p>
 * <pre>
 * &lt;config&gt;
 *     &lt;classes&gt;
 *         &lt;class&gt;
 *             &lt;name&gt;Car&lt;/name&gt;
 *             &lt;dataFolder&gt;/path/to/data/car&lt;/dataFolder&gt;
 *         &lt;/class&gt;
 *         &lt;class&gt;
 *             &lt;name&gt;Walking&lt;/name&gt;
 *             &lt;dataFolder&gt;/path/to/data/walking_fast&lt;/dataFolder&gt;
 *             &lt;dataFolder&gt;/path/to/data/walking_slow&lt;/dataFolder&gt;
 *         &lt;/class&gt;
 *     &lt;/classes&gt;
 *     &lt;params&gt;
 *         &lt;trackFilter type="LimitPerClass"&gt;
 *             &lt;param name="limit"&gt;5&lt;/param&gt;
 *         &lt;/trackFilter&gt;
 *         &lt;trainingSetSize&gt;0.6&lt;/trainingSetSize&gt;
 *         &lt;waypointFilter type="Limit"&gt;
 *             &lt;param name="limit"&gt;2&lt;/param&gt;
 *         &lt;/waypointFilter&gt;
 *     &lt;/params&gt;
 *     &lt;features&gt;
 *         &lt;feature type="MaxSpeed" /&gt;
 *         &lt;feature type="AvgSpeed" /&gt;
 *         &lt;feature type="Segments" /&gt;
 *         &lt;feature type="AvgBearingChange"&gt;
 *             &lt;param name="bearingChangeThreshold"&gt;0&lt;/param&gt;
 *         &lt;/feature&gt;
 *         &lt;feature type="MinPrecision" /&gt;
 *         &lt;feature type="MaxPrecision" /&gt;
 *         &lt;feature type="AvgPrecision" /&gt;
 *     &lt;/features&gt;
 * &lt;/config&gt;
 * </pre>
 *
 * Feature-, TrackFilter- and WaypointFilter-instances are created based on the
 * data in the config-file. Therefore the FeatureFactory, TrackFilterFactory and
 * WaypointFilterFactory are used which create the instances based on names
 * (which are taken from the config-file).
 *
 * @see de.fub.agg2graph.gpseval.Config
 */
public class ConfigFile implements Config {

    private Document mDoc;
    private Map<String, List<String>> mClassesFolderMapping;
    private List<Feature> mFeatures;
    private List<TrackFilter> mTrackFilters;
    private List<WaypointFilter> mWaypointFilters;

    /**
     * Create ConfigFile-instance based on the config-file specified by
     * <code>file</code>.
     *
     * @param file The path to the (xml-)config-file.
     * @throws IOException
     * @throws DocumentException
     */
    public ConfigFile(Path file) throws IOException, DocumentException {
        SAXReader reader = new SAXReader();

        try (InputStream is = new FileInputStream(file.toFile())) {
            mDoc = reader.read(is);
        } catch (IOException | DocumentException ex) {
            throw ex;
        }
    }

    /**
     * Get a string from the config-file. If the string referenced by the
     * <code>xpath</code> does not exists, the
     * <code>defaultValue</code> is returned.
     *
     * @param xpath
     * @param defaultValue
     * @return
     */
    public String getStr(String xpath, String defaultValue) {
        return mDoc.selectSingleNode(xpath) != null ? mDoc.selectSingleNode(xpath).getText() : defaultValue;
    }

    /**
     * Get an integer from the config-file. If the integer referenced by the
     * <code>xpath</code> does not exists, the
     * <code>defaultValue</code> is returned.
     *
     * @param xpath
     * @param defaultValue
     * @return
     */
    public int getInt(String xpath, int defaultValue) {
        return mDoc.selectSingleNode(xpath) != null ? Integer.parseInt(mDoc.selectSingleNode(xpath).getText()) : defaultValue;
    }

    /**
     * Get a double from the config-file. If the double referenced by the
     * <code>xpath</code> does not exists, the
     * <code>defaultValue</code> is returned.
     *
     * @param xpath
     * @param defaultValue
     * @return
     */
    public double getDouble(String xpath, double defaultValue) {
        return mDoc.selectSingleNode(xpath) != null ? Double.parseDouble(mDoc.selectSingleNode(xpath).getText()) : defaultValue;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Map<String, List<String>> getClassesFolderMapping() {
        if (mClassesFolderMapping != null) {
            return mClassesFolderMapping;
        }

        mClassesFolderMapping = new HashMap<>();
        List<Node> classNodes = mDoc.selectNodes("/config/classes/class");

        for (Node classNode : classNodes) {
            String className = classNode.selectSingleNode("name").getText();
            List<String> dataFolders = new LinkedList<>();

            List<Node> dataFolderNodes = classNode.selectNodes("dataFolder");

            for (Node dataFolder : dataFolderNodes) {
                dataFolders.add(dataFolder.getText());
            }

            mClassesFolderMapping.put(className, dataFolders);
        }

        return mClassesFolderMapping;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Feature> getFeatures() {
        if (mFeatures != null) {
            return mFeatures;
        }

        mFeatures = new ArrayList<>();

        FeatureFactory featureFactory = FeatureFactory.getFactory();

        List<Node> featureNodes = mDoc.selectNodes("/config/features/feature");

        for (Node featureNode : featureNodes) {
            Node typeNode = featureNode.selectSingleNode("@type");
            if (typeNode == null) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.WARNING, "Found invalid feature-node in configuration file!");
                continue;
            }

            String featureName = typeNode.getText();
            Feature feature = featureFactory.newFeature(featureName);
            if (feature == null) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, "Failed to create Feature-Object ({0})!", featureName);
                continue;
            }

            decorateWithParams(feature, featureNode);

            mFeatures.add(feature);
        }


        return mFeatures;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<TrackFilter> getTrackFilters() {
        if (mTrackFilters != null) {
            return mTrackFilters;
        }

        mTrackFilters = new ArrayList<>();

        TrackFilterFactory trackFilters = TrackFilterFactory.getFactory();

        List<Node> filterNodes = mDoc.selectNodes("/config/params/trackFilter");

        for (Node filterNode : filterNodes) {
            Node typeNode = filterNode.selectSingleNode("@type");
            if (typeNode == null) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.WARNING, "Found invalid track-filter-node in configuration file!");
                continue;
            }

            String filterName = typeNode.getText();
            TrackFilter trackFilter = trackFilters.newTrackFilter(filterName);
            if (trackFilter == null) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, "Failed to create TrackFilter-Object ({0})!", filterName);
                continue;
            }

            decorateWithParams(trackFilter, filterNode);
            trackFilter.init();


            mTrackFilters.add(trackFilter);
        }


        return mTrackFilters;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<WaypointFilter> getWaypointFilters() {
        if (mWaypointFilters != null) {
            return mWaypointFilters;
        }

        mWaypointFilters = new ArrayList<>();

        WaypointFilterFactory waypointFilters = WaypointFilterFactory.getFactory();

        List<Node> filterNodes = mDoc.selectNodes("/config/params/waypointFilter");

        for (Node filterNode : filterNodes) {
            Node typeNode = filterNode.selectSingleNode("@type");
            if (typeNode == null) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.WARNING, "Found invalid waypoint-filter-node in configuration file!");
                continue;
            }

            String filterName = typeNode.getText();
            WaypointFilter waypointFilter = waypointFilters.newWaypointFilter(filterName);
            if (waypointFilter == null) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, "Failed to create WaypointFilter-Object ({0})!", filterName);
                continue;
            }

            decorateWithParams(waypointFilter, filterNode);


            mWaypointFilters.add(waypointFilter);
        }


        return mWaypointFilters;
    }

    @Override
    public double getTrainingSetSize() {
        return getDouble("/config/params/trainingSetSize", 0.6);
    }

    @Override
    public int getCrossValidationFolds() {
        return getInt("/config/params/crossValidationFolds", 10);
    }

    /**
     * Decorate a
     * {@link de.fub.agg2graph.gpseval.utils.Parameterizable Parameterizable}-instance
     * with the parameters set in the specified node. The node (
     * <code>parentNode</code>) must have the following format:
     *
     * <pre>
     *         &lt;someNode&gt;
     *             &lt;param name="myname1"&gt;myValue1&lt;/param&gt;
     *             &lt;param name="myname2"&gt;myValue2&lt;/param&gt;
     *         &lt;/someNode&gt;
     * </pre>
     *
     * Any number of
     * <code>param</code>-nodes are allowed.
     *
     * @param p
     * @param parentNode
     */
    @SuppressWarnings("unchecked")
	private void decorateWithParams(Parameterizable p, Node parentNode) {
        List<Node> paramNodes = parentNode.selectNodes("./param");
        if (paramNodes == null) {
            return;
        }

        for (Node paramNode : paramNodes) {
            Node paramNameNode = paramNode.selectSingleNode("@name");
            if (paramNameNode == null) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.WARNING, "Found invalid param-node in configuration file!");
                continue;
            }
            String paramName = paramNameNode.getText();
            String paramValue = paramNode.getText();
            p.setParam(paramName, paramValue);
        }
    }

    @Override
    public String getName() {
        return getStr("/config/name", "### Unknown ###");
    }
}