/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource.spi;

import org.openide.explorer.view.CheckableNode;

/**
 *
 * @author Serdar
 */
public interface TrackSegmentBehaviour extends CheckableNode {

    public boolean isVisible();

    public void setVisible(boolean visible);
}
