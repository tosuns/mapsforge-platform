package de.fub.agg2graph.gpseval.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A Parameterizable is a class that accepts paramaters to be set.
 */
public abstract class Parameterizable {

    private Map<String, String> mParams = new HashMap<>();

    /**
     * Set the parameter specified by param to the given value.
     *
     * @param param
     * @param value
     */
    public void setParam(String param, String value) {
        mParams.put(param, value);
    }

    /**
     * Get the parameter-value specified by the parameters name. If the paramter
     * does not exist the defaultValue is returned.
     *
     * @param param
     * @param defaultValue
     * @return
     */
    public String getParam(String param, String defaultValue) {
        return mParams.get(param) != null ? mParams.get(param) : defaultValue;
    }

    /**
     * Get the integer parameter-value specified by the parameters name. If the
     * paramter does not exist the defaultValue is returned.
     *
     * @param param
     * @param defaultValue
     * @return
     */
    public int getIntParam(String param, int defaultValue) {
        return mParams.get(param) != null ? Integer.parseInt(mParams.get(param)) : defaultValue;
    }

    /**
     * Get the double parameter-value specified by the parameters name. If the
     * paramter does not exist the defaultValue is returned.
     *
     * @param param
     * @param defaultValue
     * @return
     */
    public double getDoubleParam(String param, double defaultValue) {
        return mParams.get(param) != null ? Double.parseDouble(mParams.get(param)) : defaultValue;
    }
}
