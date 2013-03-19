package de.fub.agg2graph.gpseval.data.filter;

import de.fub.agg2graph.gpseval.data.file.TrackFile;
import java.util.HashMap;
import java.util.Map;

/**
 * A TrackFilter used to limit the number of tracks per class.
 *
 * It has one parameter "limit" that specifies the number of tracks, that passes
 * the filter for each class.
 */
public class LimitPerClassTrackFilter extends TrackFilter {

    private Map<String, Integer> mCounts = new HashMap<>();
    private int mLimit = 0;

    @Override
    public void init() {
        mLimit = getIntParam("limit", 0);
    }

    @Override
    public boolean filter(TrackFile trackFile, String className) {
        Integer count = mCounts.get(className);
        if (count == null) {
            count = 0;
        }

        boolean res = count < mLimit;

        if (res) {
            count++;
            mCounts.put(className, count);
        }

        return res;
    }
}
