/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.color;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author Serdar
 */
public class ColorUtil {

    public synchronized static ColorProvider createColorProvider() {
        return new ColorProviderImpl();
    }

    public synchronized static Color lighten(Color color, double strength) {
        int red = (int) ((color.getRed() * (1 - strength) / 255 + strength) * 255);
        int green = (int) ((color.getGreen() * (1 - strength) / 255 + strength) * 255);
        int blue = (int) ((color.getBlue() * (1 - strength) / 255 + strength) * 255);
        return new Color(red, green, blue);
    }

    public interface ColorProvider {

        public Color getNextColor();
    }

    public static class ColorProviderImpl implements ColorProvider {

        private static final Logger LOG = Logger.getLogger(ColorUtil.class.getName());
        private int lastHueValue = 0;
        private int lastBrightnessStep = 0;
        private int lastBrightnessValue = 255;
        private int lastSaturationValue = 255;

        public Color getRandomColor() {
            Color color = Color.white;

            color = Color.getHSBColor(Math.min(1, Math.max(0, 1f / 360 * (lastHueValue % 360))), 1f, Math.max(0, 1f / 255 * lastBrightnessValue));
            lastHueValue += 17;
            if (lastBrightnessStep != lastHueValue / 360) {
                lastBrightnessValue -= 35;
                lastBrightnessStep++;

                if (lastBrightnessValue == 0) {
                    lastBrightnessValue = 255;
                } else if (lastBrightnessValue < 0) {
                    lastBrightnessValue = 0;
                }
            }
            LOG.info(MessageFormat.format("lastHueValue = {0}\nlastBrightnessValue = {1}\nLastBrightnessStep = {2}\nlastSatrationValue = {3}",
                    lastHueValue,
                    lastBrightnessValue,
                    lastBrightnessStep,
                    lastSaturationValue));
            return color;
        }

        @Override
        public Color getNextColor() {
            return getRandomColor();
        }
    }
}
