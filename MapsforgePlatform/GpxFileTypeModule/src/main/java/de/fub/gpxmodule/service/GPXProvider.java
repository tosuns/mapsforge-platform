/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.service;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer2;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Serdar
 */
public interface GPXProvider extends ModelSynchronizer2<Gpx> {

    /**
     * Return an gpx instance.
     *
     * @return Gpx instance if the underling xml file could successfully be
     * parsed, otherwise null.
     */
    public Gpx getGpx();

    /**
     * Adds a listener to get notified if the underlying xml file gets modified
     *
     * @param listener
     */
    @Override
    public void addChangeListener(ChangeListener listener);

    /**
     * remove a listener from this provider.
     *
     * @param listener
     */
    @Override
    public void removeChangeListener(ChangeListener listener);
}
