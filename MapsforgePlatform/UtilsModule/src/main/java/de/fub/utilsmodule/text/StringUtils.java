/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.text;

/**
 *
 * @author Serdar
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Globally available utility classes, mostly for string manipulation.
 *
 * 19.03.2013: Changed the functions to work without FontMetrics
 *
 * @author Jim Menard, <a href="mailto:jimm@io.com">jimm@io.com</a>, Serdar
 * Tosun
 */
public class StringUtils {

    private static final short MAX_LENGTH = 90;

    public static String StringAsHtmlWrapString(String string) {
        List<String> wrap = wrap(string, MAX_LENGTH);
        StringBuilder stringBuilder = new StringBuilder("<html>");
        for (String line : wrap) {
            stringBuilder.append(line).append("<br/>");
        }
        stringBuilder.append("</html>");
        return stringBuilder.toString();
    }

    public static String wrapString(String str) {
        return wrapString(str, MAX_LENGTH);
    }

    public static String wrapString(String str, int maxWidth) {
        List<String> wrap = wrap(str, maxWidth);
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : wrap) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Returns an array of strings, one for each line in the string after it has
     * been wrapped to fit lines of <var>maxWidth</var>. Lines end with any of
     * cr, lf, or cr lf. A line ending at the end of the string will not output
     * a further, empty string.
     * <p>
     * This code assumes <var>str</var> is not
     * <code>null</code>.
     *
     * @param str the string to split
     * @param fm needed for string width calculations
     * @param maxWidth the max line width, in points
     * @return a non-empty list of strings
     */
    public static List<String> wrap(String str, int maxWidth) {
        List<String> lines = splitIntoLines(str);
        if (lines.isEmpty()) {
            return lines;
        }

        ArrayList<String> strings = new ArrayList<String>();
        for (Iterator<String> iter = lines.iterator(); iter.hasNext();) {
            wrapLineInto(iter.next(), strings, maxWidth);
        }
        return strings;
    }

    /**
     * Given a line of text and font metrics information, wrap the line and add
     * the new line(s) to <var>list</var>.
     *
     * @param line a line of text
     * @param list an output list of strings
     * @param fm font metrics
     * @param maxWidth maximum width of the line(s)
     */
    public static void wrapLineInto(String line, List<String> list, int maxWidth) {
        int len = line.length();
        while (len > maxWidth) {
            // Guess where to split the line. Look for the next space before
            // or after the guess.
            int pos;
            if (len > maxWidth) // Too long
            {
                pos = findBreakBefore(line, maxWidth);
            } else { // Too short or possibly just right
                pos = len;
            }
            list.add(line.substring(0, pos).trim());
            line = line.substring(pos).trim();
            len = line.length();
        }
        if (len > 0) {
            list.add(line);
        }
    }

    /**
     * Returns the index of the first whitespace character or '-' in
     * <var>line</var>
     * that is at or before <var>start</var>. Returns -1 if no such character is
     * found.
     *
     * @param line a string
     * @param start where to star looking
     */
    public static int findBreakBefore(String line, int start) {
        for (int i = start; i >= 0; --i) {
            char c = line.charAt(i);
            if (Character.isWhitespace(c) || c == '-') {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the first whitespace character or '-' in
     * <var>line</var>
     * that is at or after <var>start</var>. Returns -1 if no such character is
     * found.
     *
     * @param line a string
     * @param start where to star looking
     */
    public static int findBreakAfter(String line, int start) {
        int len = line.length();
        for (int i = start; i < len; ++i) {
            char c = line.charAt(i);
            if (Character.isWhitespace(c) || c == '-') {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns an array of strings, one for each line in the string. Lines end
     * with any of cr, lf, or cr lf. A line ending at the end of the string will
     * not output a further, empty string.
     * <p>
     * This code assumes <var>str</var> is not
     * <code>null</code>.
     *
     * @param str the string to split
     * @return a non-empty list of strings
     */
    public static List<String> splitIntoLines(String str) {
        ArrayList<String> strings = new ArrayList<String>();
        if (str != null) {
            int len = str.length();
            if (len == 0) {
                strings.add("");
                return strings;
            }

            int lineStart = 0;

            for (int i = 0; i < len; ++i) {
                char c = str.charAt(i);
                if (c == '\r') {
                    int newlineLength = 1;
                    if ((i + 1) < len && str.charAt(i + 1) == '\n') {
                        newlineLength = 2;
                    }
                    strings.add(str.substring(lineStart, i));
                    lineStart = i + newlineLength;
                    if (newlineLength == 2) // skip \n next time through loop
                    {
                        ++i;
                    }
                } else if (c == '\n') {
                    strings.add(str.substring(lineStart, i));
                    lineStart = i + 1;
                }
            }
            if (lineStart < len) {
                strings.add(str.substring(lineStart));
            }
        }
        return strings;
    }
}
