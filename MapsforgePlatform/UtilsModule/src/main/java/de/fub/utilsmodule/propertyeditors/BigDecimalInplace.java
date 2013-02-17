/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    private JTextField field = new JTextField();
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
