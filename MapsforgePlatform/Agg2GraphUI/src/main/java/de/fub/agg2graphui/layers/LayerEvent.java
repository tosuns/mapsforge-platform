/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

/**
 *
 * @author Serdar
 */
public class LayerEvent {
    private Object source;

    public LayerEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
