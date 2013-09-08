/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.utilsmodule.color;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Utility class to create a ColorProvider.
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

    /**
     * interface to provide colors.
     */
    public interface ColorProvider {

        public Color getNextColor();
    }

    /**
     * Default implementation of a ColorProvider. This implementation generates
     * color via the HSB color model.
     */
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
            LOG.fine(MessageFormat.format("lastHueValue = {0}\nlastBrightnessValue = {1}\nLastBrightnessStep = {2}\nlastSatrationValue = {3}",
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
