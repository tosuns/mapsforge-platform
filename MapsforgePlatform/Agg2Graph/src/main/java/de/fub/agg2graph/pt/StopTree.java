/**
 * *****************************************************************************
 * Copyright 2013 Sebastian Müller
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
package de.fub.agg2graph.pt;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;
import com.viniciusfortuna.transit.gtfs.GtfsReader;
import com.viniciusfortuna.transit.gtfs.GtfsSpec;
import com.viniciusfortuna.transit.gtfs.Stop;
import de.fub.agg2graph.structs.GPSCalc;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Sebastian Müller
 *
 * This class creates an R-Tree from GTFS stops.txt files and provides the
 * possibility to search for nearest Stops.
 *
 */
public class StopTree {

    private final static Level LOGLEVEL = Level.INFO;
    private final static String LOGLAYOUT = "%-5p [%t]: %m%n";
    private final static String RELPATHTOZIP = "/com/viniciusfortuna/transit/gtfs/BVG.zip";
    private final static float MAXDISTANCE = 5.5F;
    private SpatialIndex si = new RTree();
    private PTIntProcedure proc = new PTIntProcedure();
    private HashMap<Integer, Stop> stopsMeta = new HashMap<Integer, Stop>();

    public void init() {
        init(LOGLEVEL, RELPATHTOZIP);
    }

    public void init(Level logLevel) {
        init(logLevel, RELPATHTOZIP);
    }

    public void init(String pathToZip) {
        init(LOGLEVEL, pathToZip);
    }

    /**
     * @param logLevel The Log4J LogLevel
     * @param pathToZip The relative Path to a GTFS Zip File
     */
    public void init(Level logLevel, String pathToZip) {
        final Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(logLevel);

        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                LOGLAYOUT)));

        Properties p = new Properties();
        p.setProperty("MinNodeEntries", "10");
        p.setProperty("MaxNodeEntries", "50");
        si.init(p);
        try {
            InputStream is = GtfsReader.class.getResourceAsStream(pathToZip);
            GtfsSpec spec = GtfsReader.readGtfsFile(is);
            List<Stop> list = spec.getStops();
            Iterator<Stop> it = list.iterator();
            int id = 1;
            while (it.hasNext()) {
                Stop stop = it.next();
                Rectangle rec = new Rectangle();
                rec.set((float) stop.longitude, (float) stop.latitude, (float) stop.longitude, (float) stop.latitude);
                stopsMeta.put(id, stop);
                si.add(rec, id);
                id++;
            }
        } catch (NumberFormatException e) {
            rootLogger.error(e.getMessage());
        } catch (MalformedURLException e) {
            rootLogger.error(e.getMessage());
        } catch (IOException e) {
            rootLogger.error(e.getMessage());
        }
    }

    /**
     * @param p The point to which the nearest stop shall be found
     * @return The distance to the nearest stop.
     */
    public Double getNearestDistance(Point p) {
        Stop stop = getNearestStop(p);
        return stop == null ? MAXDISTANCE : GPSCalc.getDistance(p.y, p.x, stop.latitude, stop.longitude);
    }

    public Stop getNearestStop(Point p) {
        return getNearestStop(p, MAXDISTANCE);
    }

    /**
     * @param p The Point to which the nearest Stop should be found.
     * @param maxDistance The maximum search distance to limit search.
     * @return The nearest Stop.
     */
    public Stop getNearestStop(Point p, float maxDistance) {
        Integer id = getNearest(p, maxDistance);
        Stop stop = stopsMeta.get(id);
        return stop;
    }

    public Integer getNearest(Point p) {
        return getNearest(p, MAXDISTANCE);
    }

    public Integer getNearest(Point p, float maxDistance) {
        si.nearest(p, proc, maxDistance);
        return proc.getId();
    }
}
