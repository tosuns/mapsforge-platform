/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.MutableWaypoint;
import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import java.text.MessageFormat;
import java.util.logging.Logger;
import junit.framework.Assert;

/**
 *
 * @author Serdar
 */
public class HeadingChangeRateFeatureTest {

    private static final Logger LOG = Logger.getLogger(HeadingChangeRateFeatureTest.class.getName());

    public HeadingChangeRateFeatureTest() {
    }

    /**
     * Test of addWaypoint method, of class HeadingChangeRateFeature.
     */
    @org.junit.Test
    public void testHeadingCompute() {
        LOG.info("HeadingCompute");
        HeadingChangeRateFeature instance = new HeadingChangeRateFeature();
        Waypoint firstWaypoint = createFirstWaypoint();
        Waypoint secondWaypoint = createSecondWaypoint();
        Waypoint thirdWaypoint = createThirdWaypoint();
        double heading = GPSCalc.computeHeading(firstWaypoint, secondWaypoint, thirdWaypoint);

        LOG.info(MessageFormat.format("heading2: {0}°", heading));
        Assert.assertEquals(45, heading, 0.01);

    }

    private Waypoint createFirstWaypoint() {
        MutableWaypoint waypoint = new MutableWaypoint();
        waypoint.setLat(2);
        waypoint.setLon(3);
        return waypoint;
    }

    private Waypoint createSecondWaypoint() {
        MutableWaypoint waypoint = new MutableWaypoint();
        waypoint.setLat(4);
        waypoint.setLon(3);
        return waypoint;
    }

    private Waypoint createThirdWaypoint() {
        MutableWaypoint waypoint = new MutableWaypoint();
        waypoint.setLat(6);
        waypoint.setLon(5);
        return waypoint;
    }
    /**
     * Test of getResult method, of class HeadingChangeRateFeature.
     */
//    @org.junit.Test
//    public void testGetResult() {
//        System.out.println("getResult");
//        HeadingChangeRateFeature instance = new HeadingChangeRateFeature();
//        double expResult = 0.0;
//        double result = instance.getResult();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
