/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.api.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Serdar
 */
public interface StatisticProvider {

    public String getName();

    public String getDescription();

    /**
     * Provides statistical data as a sorted map.
     *
     * @return SortedMap where the key ist the name of the value and the value
     * is itself.
     * @throws
     * de.fub.mapsforge.project.api.StatisticProvider.StatisticNotAvailableException
     * - StatisticNotAvailableException. If currently there are no data
     * available. The message of the exception should contain the reason why no
     * data is available and should provide a hint how to solve the problem.
     */
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException;

    public static class StatisticSection {

        private String name;
        private String description;
        private List<StatisticItem> statisticsItems = new ArrayList<StatisticItem>();

        public StatisticSection(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public StatisticSection(String name, String description, List<StatisticItem> statisticsItems) {
            this(name, description);
            this.statisticsItems.addAll(statisticsItems);
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<StatisticItem> getStatisticsItemList() {
            return statisticsItems;
        }
    }

    public static class StatisticItem {

        private final String name;
        private final String value;
        private final String description;

        public StatisticItem(String name, String value, String description) {
            this.name = name;
            this.value = value;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
            hash = 89 * hash + (this.description != null ? this.description.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StatisticItem other = (StatisticItem) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
                return false;
            }
            if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "StatisticItem{" + "name=" + name + ", value=" + value + ", description=" + description + '}';
        }
    }

    public static class StatisticNotAvailableException extends Exception {

        private static final long serialVersionUID = 1L;

        public StatisticNotAvailableException(String s) {
            super(s);
        }

        public StatisticNotAvailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
