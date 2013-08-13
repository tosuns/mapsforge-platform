/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.factories.nodes.properties;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Serdar
 */
public class InterfacePropertyEditorSupport extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InterfaceInplaceEditor ed;
    private final Class<?> interf;

    public InterfacePropertyEditorSupport(Class<?> interf) {
        this.interf = interf;
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new InterfaceInplaceEditor(interf);
            ed.getComboBox().addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getItem() instanceof ClassWrapper) {
                        setValue(e.getItem());
                    }
                }
            });
        }
        return ed;
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public Object getValue() {
        Object value = super.getValue(); //To change body of generated methods, choose Tools | Templates.
        return value;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        getInplaceEditor().getComponent().repaint();
        getInplaceEditor().getComponent().paint(gfx);
    }
}
