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
package de.fub.maps.project.api.statistics;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface to provide statistic.
 *
 * @author Serdar
 */
public interface StatisticProvider {

    /**
     * The name of this statistic provider.
     *
     * @return String the name, null not permitted.
     */
    public String getName();

    /**
     * A description for this statistic provider.
     *
     * @return String the description of this provider, null not permitted.
     */
    public String getDescription();

    /**
     * Provides statistical data as a sorted map.
     *
     * @return SortedMap where the key ist the name of the value and the value
     * is itself.
     * @throws
     * de.fub.maps.project.api.statistics.StatisticProvider.StatisticNotAvailableException
     * If currently there are no data available. The message of the exception
     * should contain the reason why no data is available and should provide a
     * hint how to solve the problem.
     */
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException;

    /**
     * Returns a visual representation. The provider itself is responsible to
     * update/refresh the visual representation.
     *
     * @return a Component if this Provider support it, otherwise null.
     */
    public Component getVisualRepresentation();

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
            return MessageFormat.format("StatisticItem[name={0}, value={1}, description={2}]", name, value, description);
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
