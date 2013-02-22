/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.text;

import java.text.MessageFormat;
import java.util.Locale;

/**
 *
 * @author Serdar
 */
public class MessageFormatter {

    public synchronized static String format(String pattern, Object... arguments) {
        return new MessageFormat(pattern).format(arguments, new StringBuffer(), null).toString();
    }

    public synchronized static String format(Locale locale, String pattern, Object... arguments) {
        return new MessageFormat(pattern, locale).format(arguments, new StringBuffer(), null).toString();
    }
}
