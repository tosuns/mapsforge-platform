/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.api;

import java.util.SortedMap;

/**
 *
 * @author Serdar
 */
public interface StatisticProvider {

    /**
     * Provides statistical data as a sorted map.
     *
     * @return SortedMap where the key ist the name of the value and the value
     * is itself.
     * @exception StatisticNotAvailableException. If currently there are no data
     * available. The message of the exception should contain the reason why no
     * data is available and should provide a hint how to solve the problem.
     */
    public SortedMap<String, Double> getStatisticData();

    public static class StatisticNotAvailableException extends IllegalStateException {

        private static final long serialVersionUID = 1L;

        public StatisticNotAvailableException(String s) {
            super(s);
        }

        public StatisticNotAvailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
