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
package de.fub.maps.project.openstreetmap.service;

import javax.swing.event.ChangeListener;

/**
 *
 * @author Serdar
 */
public interface LocationBoundingBoxService {

    public BoundingBox getViewBoundingBox();

    public void addChangeListener(ChangeListener listener);

    public void removeChangeListener(ChangeListener listener);

    public static class BoundingBox {

        private final double leftLongitude;
        private final double bottomLatitude;
        private final double rightLongitude;
        private final double topLatitude;

        public BoundingBox(double topLatitude, double leftLongitude, double bottomLatitude, double rightLongitude) {
            this.leftLongitude = leftLongitude;
            this.bottomLatitude = bottomLatitude;
            this.rightLongitude = rightLongitude;
            this.topLatitude = topLatitude;
        }

        public double getLeftLongitude() {
            return leftLongitude;
        }

        public double getBottomLatitude() {
            return bottomLatitude;
        }

        public double getRightLongitude() {
            return rightLongitude;
        }

        public double getTopLatitude() {
            return topLatitude;
        }
    }
}
