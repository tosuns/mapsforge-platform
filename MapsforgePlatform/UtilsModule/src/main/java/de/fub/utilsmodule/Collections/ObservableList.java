/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.Collections;

import java.util.List;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Serdar
 */
public interface ObservableList<T> extends List<T> {

    public void addChangeListener(ChangeListener listener);

    public void removeChangeListener(ChangeListener listener);
}
