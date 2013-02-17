/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Serdar
 */
public class DateAdapter {

    public static Date parseDate(String s) {
        return DatatypeConverter.parseDateTime(s).getTime();
    }

    public static String printDate(Date dt) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTime(dt);
        String printDate = DatatypeConverter.printDateTime(cal);
        
        return printDate;
    }
}
