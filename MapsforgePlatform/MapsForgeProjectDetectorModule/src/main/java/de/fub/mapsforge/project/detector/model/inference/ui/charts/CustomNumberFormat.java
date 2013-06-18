/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui.charts;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 *
 * @author Serdar
 */
public class CustomNumberFormat extends NumberFormat {

    private static final long serialVersionUID = 1L;
    private NumberFormat def = NumberFormat.getPercentInstance();

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return new StringBuffer(MessageFormat.format("{0, number, 0.00}", number));
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return new StringBuffer(MessageFormat.format("{0, number, 0.00}", number));
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        return def.parse(source, parsePosition);
    }
}
