/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;

/**
 *
 * @author Serdar
 */
public class Point implements Drawable {

    private ILocation at;
    private RenderingOptions renderingOptions;

    public Point(ILocation at, RenderingOptions ro) {
        this.at = at;
        this.renderingOptions = ro;
    }

    @Override
    public RenderingOptions getRenderingOptions() {
        return renderingOptions;
    }

    /**
     * @return the at
     */
    public ILocation getAt() {
        return at;
    }

    /**
     * @param at the at to set
     */
    public void setAt(ILocation at) {
        this.at = at;
    }

    /**
     * @param renderingOptions the renderingOptions to set
     */
    public void setRenderingOptions(RenderingOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }
}
