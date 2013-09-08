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
