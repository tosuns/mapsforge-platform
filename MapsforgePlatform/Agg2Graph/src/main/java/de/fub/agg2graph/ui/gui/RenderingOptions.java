/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
package de.fub.agg2graph.ui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class RenderingOptions {

    public static final String PROP_NAME_Z_INDEX = "zindex";
    public static final String PROP_NAME_COLOR = "color";
    public static final String PROP_NAME_STROKE_BASE_WIDTH_FACTOR = "strokeBaseWidthFactor";
    public static final String PROP_NAME_LABEL_RENDERING_TYPE = "labelRenderingType";
    public static final String PROP_NAME_OPACITY = "opacity";
    public static final String PROP_NAME_RENDERING_TYPE = "renderingType";
    private int zIndex = 0;
    private Color color = Color.BLACK;
    private float strokeBaseWidthFactor = 1;
    private static BasicStroke basicStroke = new BasicStroke(3.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    private LabelRenderingType labelRenderingType = LabelRenderingType.NEVER;
    private double opacity = 1;
    private RenderingType renderingType = RenderingType.INTELLIGENT_ALL;
    private Map<Float, Stroke> strokes = new HashMap<Float, Stroke>();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * @return the basicStroke
     */
    public static BasicStroke getBasicStroke() {
        return basicStroke;
    }

    /**
     * @return the zIndex
     */
    public int getzIndex() {
        return zIndex;
    }

    /**
     * @param zIndex the zIndex to set
     */
    public void setzIndex(int zIndex) {
        Object oldValue = this.zIndex;
        this.zIndex = zIndex;
        pcs.firePropertyChange(PROP_NAME_Z_INDEX, oldValue, this.zIndex);
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        Object oldValue = this.color;
        this.color = color;
        pcs.firePropertyChange(PROP_NAME_COLOR, oldValue, this.color);
    }

    /**
     * @return the strokeBaseWidthFactor
     */
    public float getStrokeBaseWidthFactor() {
        return strokeBaseWidthFactor;
    }

    /**
     * @param strokeBaseWidthFactor the strokeBaseWidthFactor to set
     */
    public void setStrokeBaseWidthFactor(float strokeBaseWidthFactor) {
        Object oldValue = this.strokeBaseWidthFactor;
        this.strokeBaseWidthFactor = strokeBaseWidthFactor;
        pcs.firePropertyChange(PROP_NAME_STROKE_BASE_WIDTH_FACTOR, oldValue, this.strokeBaseWidthFactor);
    }

    /**
     * @return the labelRenderingType
     */
    public LabelRenderingType getLabelRenderingType() {
        return labelRenderingType;
    }

    /**
     * @param labelRenderingType the labelRenderingType to set
     */
    public void setLabelRenderingType(LabelRenderingType labelRenderingType) {
        Object oldValue = this.labelRenderingType;
        this.labelRenderingType = labelRenderingType;
        pcs.firePropertyChange(PROP_NAME_LABEL_RENDERING_TYPE, oldValue, this.labelRenderingType);
    }

    /**
     * @return the opacity
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(double opacity) {
        Object oldValue = this.opacity;
        this.opacity = opacity;
        pcs.firePropertyChange(PROP_NAME_OPACITY, oldValue, this.opacity);
    }

    /**
     * @return the renderingType
     */
    public RenderingType getRenderingType() {
        return renderingType;
    }

    /**
     * @param renderingType the renderingType to set
     */
    public void setRenderingType(RenderingType renderingType) {
        Object oldValue = this.renderingType;
        this.renderingType = renderingType;
        pcs.firePropertyChange(PROP_NAME_RENDERING_TYPE, oldValue, this.renderingType);
    }

    public Stroke getStroke(float weightFactor) {
        // a little cache for the strokes
        if (strokes.get(weightFactor) != null) {
            return strokes.get(weightFactor);
        }
        BasicStroke newStroke = new BasicStroke(getBasicStroke().getLineWidth()
                * getStrokeBaseWidthFactor() * weightFactor,
                getBasicStroke().getEndCap(), getBasicStroke().getLineJoin(),
                getBasicStroke().getMiterLimit(), getBasicStroke().getDashArray(),
                getBasicStroke().getDashPhase());
        // CompoundStroke finalStroke = new CompoundStroke(newStroke,
        // borderStroke, CompoundStroke.ADD);
        strokes.put(weightFactor, newStroke);
        return newStroke;
    }

    public RenderingOptions getCopy() {
        RenderingOptions result = new RenderingOptions();
        result.setzIndex(getzIndex());
        result.setColor(getColor());
        result.setStrokeBaseWidthFactor(getStrokeBaseWidthFactor());
        result.setLabelRenderingType(getLabelRenderingType());
        result.setOpacity(getOpacity());
        result.setRenderingType(getRenderingType());
        return result;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "RenderingOptions [color=" + getColor() + ", stroke=" + getBasicStroke()
                + ", opacity=" + getOpacity() + "]";
    }

    public enum RenderingType {

        ALL, INTELLIGENT_ALL, POINTS, LINES, NONE
    };

    public enum LabelRenderingType {

        ALWAYS, INTELLIGENT, NEVER
    };
}
