//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.14 at 08:42:30 PM MEZ 
//


package de.fub.gpxmodule.xml.gpx;

import java.math.BigDecimal;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter5
    extends XmlAdapter<String, BigDecimal>
{


    public BigDecimal unmarshal(String value) {
        return (de.fub.gpxmodule.DegreeAdapter.parseDegree(value));
    }

    public String marshal(BigDecimal value) {
        return (de.fub.gpxmodule.DegreeAdapter.printDegree(value));
    }

}
