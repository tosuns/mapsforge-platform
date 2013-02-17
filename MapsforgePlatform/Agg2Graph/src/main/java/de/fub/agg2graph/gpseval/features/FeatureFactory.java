package de.fub.agg2graph.gpseval.features;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FeatureFactory-instance can be used to create new Feature-instances based
 * on a feature name.
 *
 * It comes with support for all built-in Features like MaxSpeedFeature,
 * SegmentsFeature and so on. To register your own Feature-class use the
 * registerFeatureClass methods.
 *
 * @see ConfigFile for example usage.
 */
public class FeatureFactory {

    private static FeatureFactory instance = new FeatureFactory();
    private Map<String, Class<? extends Feature>> mFeatureClasses = new HashMap<>();

    private FeatureFactory() {
        registerBuiltInFeatures();
    }

    /**
     * Register the built-in Feature-classes.
     */
    private void registerBuiltInFeatures() {
        registerFeatureClass(AvgBearingChangeFeature.class);
        registerFeatureClass(AvgPrecisionFeature.class);
        registerFeatureClass(AvgSpeedFeature.class);
        registerFeatureClass(AvgTransportationDistanceFeature.class);

        registerFeatureClass(MaxPrecisionFeature.class);
        registerFeatureClass(MaxSpeedFeature.class);
        registerFeatureClass(MaxAccelerationFeature.class);

        registerFeatureClass(MinPrecisionFeature.class);

        registerFeatureClass(SegmentsFeature.class);
    }

    /**
     * Returns the FeatureFactory-instance.
     *
     * @return
     */
    public static FeatureFactory getFactory() {
        return instance;
    }

    /**
     * Register a Feature-class with the given name.
     *
     * @param name
     * @param featureClass
     */
    public void registerFeatureClass(String name, Class<? extends Feature> featureClass) {
        mFeatureClasses.put(name, featureClass);
    }

    /**
     * Register a Feature-class. The Feature's identifier will be used as name.
     *
     * @param featureClass
     */
    public void registerFeatureClass(Class<? extends Feature> featureClass) {
        mFeatureClasses.put(Feature.getFeatureIdentifier(featureClass), featureClass);
    }

    /**
     * Get the Feature-class for the given name.
     *
     * @param name
     * @return
     */
    public Class<? extends Feature> getFeatureClass(String name) {
        return mFeatureClasses.get(name);
    }

    /**
     * Return a new Feature-instance for the given Feature-name.
     *
     * @param name
     * @return
     */
    public Feature newFeature(String name) {
        Feature feature = null;
        Class<? extends Feature> featureClass = mFeatureClasses.get(name);

        if (featureClass != null) {
            try {
                feature = featureClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(FeatureFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return feature;
    }
}
