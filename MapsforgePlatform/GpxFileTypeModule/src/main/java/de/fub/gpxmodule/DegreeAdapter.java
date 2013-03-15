/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule;

import java.math.BigDecimal;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Serdar
 */
public class DegreeAdapter {

    public static BigDecimal parseDegree(String s) {
        return new BigDecimal(s);
    }

    public static String printDegree(BigDecimal dt) {
        String printDate = DatatypeConverter.printDecimal(dt);

        return printDate;
    }
}
