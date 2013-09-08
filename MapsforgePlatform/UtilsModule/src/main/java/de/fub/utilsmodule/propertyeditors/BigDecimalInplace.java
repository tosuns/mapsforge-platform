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
package de.fub.utilsmodule.propertyeditors;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.math.BigDecimal;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Serdar
 */
public class BigDecimalInplace implements InplaceEditor {

    private final JTextField field = new JTextField();
    private PropertyEditor editor = null;
    private PropertyModel model;

    @Override
    public void connect(PropertyEditor pe, PropertyEnv pe1) {
        editor = pe;
        reset();
    }

    @Override
    public JComponent getComponent() {
        return field;
    }

    @Override
    public void clear() {
        //avoid memory leaks:
        editor = null;
        model = null;
    }

    @Override
    public Object getValue() {
        BigDecimal value = BigDecimal.ZERO;
        String text = field.getText();
        if (text != null) {
            value = new BigDecimal(text);
        }
        return value;
    }

    @Override
    public void setValue(Object o) {
        if (o instanceof String) {
            field.setText((String) o);
        } else if (o instanceof BigDecimal) {
            field.setText(((BigDecimal) o).toPlainString());
        }
    }

    @Override
    public boolean supportsTextEntry() {
        return true;
    }

    @Override
    public void reset() {
        Object value = editor.getValue();
        if (value instanceof String) {
            field.setText((String) value);
        } else if (value instanceof BigDecimal) {
            field.setText(((BigDecimal) value).toPlainString());
        }
    }

    @Override
    public void addActionListener(ActionListener al) {
        //do nothing - not needed for this component
    }

    @Override
    public void removeActionListener(ActionListener al) {
        //do nothing - not needed for this component
    }

    @Override
    public KeyStroke[] getKeyStrokes() {
        return new KeyStroke[0];
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return editor;
    }

    @Override
    public PropertyModel getPropertyModel() {
        return model;
    }

    @Override
    public void setPropertyModel(PropertyModel pm) {
        this.model = pm;
    }

    @Override
    public boolean isKnownComponent(Component cmpnt) {
        return cmpnt == field || field.isAncestorOf(cmpnt);
    }
}
