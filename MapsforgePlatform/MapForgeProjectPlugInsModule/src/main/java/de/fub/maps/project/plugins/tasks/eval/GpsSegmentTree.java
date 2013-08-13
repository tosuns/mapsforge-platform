/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.plugins.tasks.eval;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import gnu.trove.TIntProcedure;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Serdar
 */
public class GpsSegmentTree {

    private RTree rtree = new RTree();
    private AtomicInteger itemID = new AtomicInteger(0);
    private final Object MUTEX = new Object();
    private final HashMap<Integer, GPSSegment> META_DATA = new HashMap<Integer, GPSSegment>(500);

    public GpsSegmentTree() {
        init();
    }

    private void init() {
        reset();
    }

    public void addSegments(Collection<GPSSegment> segments) {
        for (GPSSegment segment : segments) {
            addSegment(segment);
        }
    }

    public void addSegment(GPSSegment segment) {
        synchronized (MUTEX) {
            Rectangle boundingBox = getBoundingBox(segment);
            int id = itemID.getAndIncrement();
            rtree.add(boundingBox, id);
            META_DATA.put(id, segment);
        }
    }

    public void reset() {
        itemID.set(0);
        META_DATA.clear();
        Properties p = new Properties();
        p.setProperty("MinNodeEntries", "10");
        p.setProperty("MaxNodeEntries", "50");
        rtree = new RTree();
        rtree.init(p);
    }

    public Collection<GPSSegment> getIntersectingSegment(GPSSegment segment) {
        synchronized (MUTEX) {
            // compute the bounding box of the specified segment
            Rectangle boundingBox = getBoundingBox(segment);

            // create a callback
            GPSSegmentSeachProcedure gpsSegmentSeachProcedure = new GPSSegmentSeachProcedure();

            // search
            rtree.intersects(boundingBox, gpsSegmentSeachProcedure);

            // return results
            return gpsSegmentSeachProcedure.getSegments();
        }
    }

    private Rectangle getBoundingBox(GPSSegment segment) {
        Area area = new Area();
        for (ILocation location : segment) {
            // the added shape must have at least a size > 0
            area.add(new Area(new Rectangle2D.Double(location.getLon(), location.getLat(), 0.0000000001, 0.0000000001)));
        }
        Rectangle2D boundingBox = area.getBounds2D();
        float minLong = (float) boundingBox.getMinX();
        float maxLat = (float) boundingBox.getMinY();
        float maxLong = (float) boundingBox.getMaxX();
        float minLat = (float) boundingBox.getMaxY();
        Rectangle rectangle = new Rectangle(minLong, maxLat, maxLong, minLat);
        return rectangle;
    }

    private class GPSSegmentSeachProcedure implements TIntProcedure {

        private HashSet<GPSSegment> segments = new HashSet<GPSSegment>(100);

        public GPSSegmentSeachProcedure() {
        }

        @Override
        public boolean execute(int value) {
            GPSSegment segment = META_DATA.get(value);
            if (segment != null) {
                segments.add(segment);
            }
            return true;
        }

        public Collection<GPSSegment> getSegments() {
            return segments;
        }
    }
}
