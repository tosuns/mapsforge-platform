/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.snapshot.api;

import java.awt.Component;
import java.awt.Image;

/**
 * Interface to export a visual component of the application.
 *
 * @author Serdar
 */
public interface ComponentSnapShotExporter extends Comparable<ComponentSnapShotExporter> {

    /**
     * Returns in icon image that represents this exporter instance.
     *
     * @return A Image instance with a Dimension of 16x16.
     */
    public Image getIconImage();

    /**
     * Provides the name of this instance.
     *
     * @return A String instance, null not permitted.
     */
    public String getName();

    /**
     * Provides a short description of this instance.
     *
     * @return A String instance, null not permitted.
     */
    public String getShortDescription();

    /**
     * Export the specified Component.
     *
     * @param component a Component instance, null not permitted.
     */
    public void export(Component component);
}
