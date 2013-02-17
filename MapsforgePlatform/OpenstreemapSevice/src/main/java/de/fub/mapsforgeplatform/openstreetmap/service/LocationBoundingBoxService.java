/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.service;

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
