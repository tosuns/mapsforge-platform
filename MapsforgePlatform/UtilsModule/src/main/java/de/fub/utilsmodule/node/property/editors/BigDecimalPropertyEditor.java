/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.node.property.editors;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Serdar
 */
@PropertyEditorRegistration(targetType = BigDecimal.class)
public class BigDecimalPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            return bigDecimal.toPlainString();
        }

        return "<null value>"; //NO18N
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        BigDecimal bigDecimal = new BigDecimal(text);
        setValue(bigDecimal);
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new BigDecimalPropertyInplaceEditor();
        }
        return ed;
    }

    private static class BigDecimalPropertyInplaceEditor implements InplaceEditor {

        private JTextField textField = new JTextField();
        private PropertyEditor editor = null;
        private PropertyModel model;

        public BigDecimalPropertyInplaceEditor() {
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            editor = propertyEditor;
            reset();
        }

        @Override
        public JComponent getComponent() {
            return textField;
        }

        @Override
        public void clear() {
            editor = null;
            model = null;
        }

        @Override
        public Object getValue() {
            return textField.getText();
        }

        @Override
        public void setValue(Object o) {
            if (o instanceof String) {
                textField.setText((String) o);
            }
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void reset() {
            Object value = editor.getValue();
            if (value instanceof BigDecimal) {
                textField.setText(((BigDecimal) value).toPlainString());
            }
        }

        @Override
        public void addActionListener(ActionListener al) {
        }

        @Override
        public void removeActionListener(ActionListener al) {
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
        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }

        @Override
        public boolean isKnownComponent(Component component) {
            return component == textField || textField.isAncestorOf(component);
        }
    }
}
