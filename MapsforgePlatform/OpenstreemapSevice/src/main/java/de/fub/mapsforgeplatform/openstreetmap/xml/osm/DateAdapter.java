/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.xml.osm;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Serdar
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(String value) {
        return (de.fub.gpxmodule.DateAdapter.parseDate(value));
    }

    @Override
    public String marshal(Date value) {
        return (de.fub.gpxmodule.DateAdapter.printDate(value));
    }
}
