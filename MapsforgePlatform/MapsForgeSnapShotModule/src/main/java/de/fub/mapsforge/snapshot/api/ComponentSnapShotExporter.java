/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.api;

import java.awt.Component;
import java.awt.Image;

/**
 *
 * @author Serdar
 */
public interface ComponentSnapShotExporter {

    public Image getIconImage();

    public String getShortDescription();

    public void export(Component component);
}
