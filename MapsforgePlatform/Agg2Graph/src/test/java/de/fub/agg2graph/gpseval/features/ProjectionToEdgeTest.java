/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Serdar
 */
public class ProjectionToEdgeTest {

    private static final Logger LOG = Logger.getLogger(ProjectionToEdgeTest.class.getName());

    public ProjectionToEdgeTest() {
    }

    @Test
    public void projectionToEdgeTest() {
        firstTest();
        secondTest();
        thirdTest();
        forthTest();
    }

    private void firstTest() {
        GPSPoint x = new GPSPoint(2, 3);
        GPSPoint start = new GPSPoint(0, 0);
        GPSPoint end = new GPSPoint(2, 0);

        ILocation projectPointToEdge = GPSCalc.getProjectionPoint(x, start, end);
        LOG.info(projectPointToEdge.toString());

        projectPointToEdge = GPSCalc.getProjectedPointToEdge(x, start, end);
        LOG.info(projectPointToEdge.toString());

        GPSPoint result = new GPSPoint(2, 0);
        Assert.assertEquals(result, projectPointToEdge);
    }

    private void secondTest() {
        GPSPoint x = new GPSPoint(2, 3);
        GPSPoint start = new GPSPoint(0, 0);
        GPSPoint end = new GPSPoint(18, 0);

        ILocation projectPointToEdge = GPSCalc.getProjectedPointToEdge(x, start, end);
        LOG.info(projectPointToEdge.toString());

        projectPointToEdge = GPSCalc.getProjectedPointToEdge(x, start, end);
        LOG.info(projectPointToEdge.toString());

        GPSPoint result = new GPSPoint(2, 0);
        Assert.assertEquals(result, projectPointToEdge);
    }

    private void thirdTest() {
        GPSPoint x = new GPSPoint(-60, 35);
        GPSPoint start = new GPSPoint(39, 45);
        GPSPoint end = new GPSPoint(165, 70);

        ILocation projectPointToEdge = GPSCalc.getProjectedPointToEdge(x, start, end);
        LOG.info(projectPointToEdge.toString());

        ILocation projectPointToEdge2 = GPSCalc.getProjectedPointToEdge(x, start, end);
        LOG.info(projectPointToEdge2.toString());
        Assert.assertEquals(projectPointToEdge2, projectPointToEdge);
    }

    private void forthTest() {
        GPSPoint x = new GPSPoint(-60, 35);
        GPSPoint start = new GPSPoint(39, 45);
        GPSPoint end = new GPSPoint(165, 70);

        ILocation projectPointToEdge = GPSCalc.getProjectedPointToEdge(x, start, end);
        LOG.info(projectPointToEdge.toString());

        ILocation projectPointToEdge2 = GPSCalc.getProjectedPointToEdge(x, start, end);
        LOG.info(projectPointToEdge2.toString());
        Assert.assertEquals(projectPointToEdge2, projectPointToEdge);
    }
}
