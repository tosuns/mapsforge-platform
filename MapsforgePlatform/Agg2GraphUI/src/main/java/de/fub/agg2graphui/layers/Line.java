/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    private boolean directed;
    private String label;

    public Line(ILocation from, ILocation to, RenderingOptions ro,
            float weightFactor) {
        this(from, to, ro, weightFactor, false);
        this.label = new String();
    }

    public Line(ILocation from, ILocation to, RenderingOptions ro,
            float weightFactor, boolean directed) {
        this.label = new String();
        this.from = from;
        this.to = to;
        this.renderingOptions = ro;
        this.weightFactor = weightFactor;
        this.directed = directed;
    }

    @Override
    public RenderingOptions getRenderingOptions() {
        return renderingOptions;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }
}
