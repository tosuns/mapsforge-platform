package de.fub.agg2graph.gpseval;

import com.infomatiq.jsi.Point;
import de.fub.agg2graph.pt.StopTree;

/**
 * A (Singleton-)Wrapper for de.fub.agg2graph.pt.StopTree.
 *
 * @author mkay
 */
public class TransportationDistance {

    private StopTree mStopTree;
    private static TransportationDistance mInstance = new TransportationDistance();

    private TransportationDistance() {
        mStopTree = new StopTree();
        mStopTree.init();
    }

    public static TransportationDistance getInstance() {
        return mInstance;
    }

    public double getNearestDistance(double lat, double lon) {
        Point p = new Point((float) lon, (float) lat); // TODO loose precision?!
        mStopTree.getNearest(p);
        return mStopTree.getNearestDistance(p);
    }
}
