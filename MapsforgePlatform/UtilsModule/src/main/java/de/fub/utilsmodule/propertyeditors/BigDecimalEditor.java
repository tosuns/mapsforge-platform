/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class BigDecimalEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    @NbBundle.Messages({"Not_A_BigDecimalValue=Not a big decimal value"})
    @Override
    public String getAsText() {
        Object value = getValue();
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        return Bundle.Not_A_BigDecimalValue();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(new BigDecimal(text));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public void attachEnv(PropertyEnv pe) {
        pe.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new BigDecimalInplace();
        }
        return ed;
    }
}
