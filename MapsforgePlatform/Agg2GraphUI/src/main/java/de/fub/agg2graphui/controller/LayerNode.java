/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.controller;

import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.layers.LayerEvent;
import de.fub.agg2graphui.layers.LayerListener;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class LayerNode extends AbstractNode {

    public LayerNode(AbstractLayer<?> layer) {
        super(Children.LEAF, Lookups.singleton(layer));
        setDisplayName(layer.getName());
        setShortDescription(layer.getDescription());
        layer.addLayerListener(new LayerListener() {
            @Override
            public void requestRender(LayerEvent layerEvent) {
                fireIconChange();
            }
        });
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("Render Settings");
        set.setDisplayName("Render Settings");
        sheet.put(set);
        AbstractLayer layer = getLookup().lookup(AbstractLayer.class);

        if (layer != null) {
            final RenderingOptions renderSettings = layer.getRenderingOptions();

            Property<?> property = new PropertySupport.ReadWrite<Color>("strokeColor", Color.class, "Stroke Color", "") {
                @Override
                public Color getValue() throws IllegalAccessException, InvocationTargetException {
                    return renderSettings.getColor();
                }

                @Override
                public void setValue(Color val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    renderSettings.setColor(val);
                }
            };
            set.put(property);

            property = new PropertySupport.ReadWrite<Integer>("zIndex", Integer.class, "Z-Index", "") {
                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return renderSettings.getzIndex();
                }

                @Override
                public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    renderSettings.setzIndex(val);
                }
            };
            set.put(property);

            property = new PropertySupport.ReadWrite<Float>("strokeBaseWidthFactor", Float.class, "Stroke Base Width Factory", "") {
                @Override
                public Float getValue() throws IllegalAccessException, InvocationTargetException {
                    return renderSettings.getStrokeBaseWidthFactor();
                }

                @Override
                public void setValue(Float val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    renderSettings.setStrokeBaseWidthFactor(val);
                }
            };
            set.put(property);

            property = new PropertySupport.ReadWrite<Double>("opacity", Double.class, "Opacity", "Opacity of the layer between 0 and 1") {
                @Override
                public Double getValue() throws IllegalAccessException, InvocationTargetException {
                    return renderSettings.getOpacity();
                }

                @Override
                public void setValue(Double val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (val < 0 || val > 1) {
                        throw new IllegalArgumentException("Value must be between 0 and 1!");
                    }
                    renderSettings.setOpacity(val);
                }
            };
            set.put(property);

            property = new PropertySupport.ReadWrite<RenderingOptions.RenderingType>("renderingType", RenderingOptions.RenderingType.class, "Rendering Type", "Rendering Type") {
                @Override
                public RenderingOptions.RenderingType getValue() throws IllegalAccessException, InvocationTargetException {
                    return renderSettings.getRenderingType();
                }

                @Override
                public void setValue(RenderingOptions.RenderingType val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    renderSettings.setRenderingType(val);
                }
            };
            set.put(property);

            property = new PropertySupport.ReadWrite<RenderingOptions.LabelRenderingType>("labelRenderingType", RenderingOptions.LabelRenderingType.class, "Label Rendering Type", "Label Rendering Type") {
                @Override
                public RenderingOptions.LabelRenderingType getValue() throws IllegalAccessException, InvocationTargetException {
                    return renderSettings.getLabelRenderingType();
                }

                @Override
                public void setValue(RenderingOptions.LabelRenderingType val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    renderSettings.setLabelRenderingType(val);
                }
            };
            set.put(property);
        }

        return sheet;
    }
}
