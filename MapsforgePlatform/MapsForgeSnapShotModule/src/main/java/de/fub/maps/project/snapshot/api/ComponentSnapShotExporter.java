/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.snapshot.api;

import java.awt.Component;
import java.awt.Image;

/**
 *
 * @author Serdar
 */
public interface ComponentSnapShotExporter extends Comparable<ComponentSnapShotExporter> {

    public Image getIconImage();

    public String getName();

    public String getShortDescription();

    public void export(Component component);
}
