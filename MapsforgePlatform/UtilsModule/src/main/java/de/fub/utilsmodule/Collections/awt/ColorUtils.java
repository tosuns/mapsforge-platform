/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.Collections.awt;

import java.awt.Color;

/**
 *
 * @author Serdar
 */
public class ColorUtils {

    public synchronized static Color lighten(Color color, double strength) {
        int red = (int) ((color.getRed() * (1 - strength) / 255 + strength) * 255);
        int green = (int) ((color.getGreen() * (1 - strength) / 255 + strength) * 255);
        int blue = (int) ((color.getBlue() * (1 - strength) / 255 + strength) * 255);
        return new Color(red, green, blue);
    }
}
