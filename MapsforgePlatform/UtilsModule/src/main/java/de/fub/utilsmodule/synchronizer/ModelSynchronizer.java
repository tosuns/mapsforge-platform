/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.synchronizer;

import javax.swing.event.ChangeListener;

/**
 *
 * @author Serdar
 */
public interface ModelSynchronizer<T> {

    public void modelChanged(Object uiComponent, T model);

    public void addChangeListener(ChangeListener listener);

    public void removeChangeListener(ChangeListener listener);
}
