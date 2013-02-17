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
public class Line implements Drawable {

    private ILocation from;
    private ILocation to;
    private RenderingOptions renderingOptions;
    private float weightFactor = 1;

    public Line(ILocation from, ILocation to, RenderingOptions ro,
            float weightFactor) {
        this.from = from;
        this.to = to;
        this.renderingOptions = ro;
        this.weightFactor = weightFactor;
    }

    @Override
    public RenderingOptions getRenderingOptions() {
        return renderingOptions;
    }

    /**
     * @return the from
     */
    public ILocation getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(ILocation from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public ILocation getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(ILocation to) {
        this.to = to;
    }

    /**
     * @param renderingOptions the renderingOptions to set
     */
    public void setRenderingOptions(RenderingOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }

    /**
     * @return the weightFactor
     */
    public float getWeightFactor() {
        return weightFactor;
    }

    /**
     * @param weightFactor the weightFactor to set
     */
    public void setWeightFactor(float weightFactor) {
        this.weightFactor = weightFactor;
    }
}
