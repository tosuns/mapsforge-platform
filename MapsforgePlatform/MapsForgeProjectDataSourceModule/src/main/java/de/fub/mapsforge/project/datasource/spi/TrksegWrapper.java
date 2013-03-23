/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource.spi;

import de.fub.gpxmodule.xml.gpx.Trkseg;
import java.awt.Color;

/**
 *
 * @author Serdar
 */
public class TrksegWrapper {

    private final Color color;
    private final Trkseg trkseg;

    public TrksegWrapper(Trkseg trkseg, Color color) {
        this.color = color;
        this.trkseg = trkseg;
    }

    public Color getColor() {
        return color;
    }

    public Trkseg getTrkseg() {
        return trkseg;
    }
}
