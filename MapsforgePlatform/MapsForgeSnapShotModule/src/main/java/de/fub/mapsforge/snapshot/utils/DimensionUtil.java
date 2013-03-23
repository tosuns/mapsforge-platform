/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author Serdar
 */
public class DimensionUtil {

    /**
     * this method computes the A4 dimension in pixel unites depending on the
     * monitor screen resolution.
     *
     * @param sourceDimension
     * @return
     */
    public static Dimension computeToA4(Dimension sourceDimension) {
        Dimension destDimension = sourceDimension;

        // metric factor inch in mm
        final double metricFactor = 2.54 * 10d;

        // compute ppi value through screnn resolution and a factor. the factor is currently not
        // resolution independent.
        double ppi = Toolkit.getDefaultToolkit().getScreenResolution() * 0.9379;

        // get current sourceDimension as mm units
        double widthInMm = sourceDimension.getWidth() / ppi * metricFactor;
        double heightInMm = sourceDimension.getHeight() / ppi * metricFactor;

        // compute the factors in relation to a A4 form
        double widthFactor = widthInMm < 210 ? widthInMm / 210 : 210 / widthInMm;
        double heightFactor = heightInMm < 297 ? heightInMm / 297 : 297 / heightInMm;

        // compute the destination dimension
        int destWidth = (int) (sourceDimension.getWidth() * widthFactor);
        int destHeight = (int) (sourceDimension.getHeight() * heightFactor);
        destDimension = new Dimension(destWidth, destHeight);

        return destDimension;
    }

    /**
     * IText lib works internally with points instead of pixels. To compute the
     * resized dimension for the pdf export, we use the following function to
     * resize the dimension to A4 paper size.
     *
     * @param sourceDimesion
     * @return
     */
    public static Dimension computeToA4Pdf(Dimension sourceDimension) {
        Dimension destDimension = computeToA4(sourceDimension);
        // this is properbly not resolution independent, because of hard it is hard coded.
        destDimension.setSize(destDimension.width * 0.8, destDimension.height * 0.8);

        return destDimension;
    }
}
