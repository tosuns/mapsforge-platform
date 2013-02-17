/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import java.util.EventListener;

/**
 *
 * @author Serdar
 */
public interface LayerListener extends EventListener {

    public void requestRender(LayerEvent layerEvent);
}
