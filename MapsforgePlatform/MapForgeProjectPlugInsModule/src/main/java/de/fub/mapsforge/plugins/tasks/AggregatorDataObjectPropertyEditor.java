/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import org.openide.loaders.DataObject;

/**
 *
 * @author Serdar
 */
public class AggregatorDataObjectPropertyEditor extends PropertyEditorSupport {

    private AggregatorChooserPanel panel = null;

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return "<null value>";
        } else if (value instanceof DataObject) {
            return ((DataObject) value).getPrimaryFile().getPath();
        }
        return super.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
//        super.setAsText(text); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        if (panel == null) {
            panel = new AggregatorChooserPanel(AggregatorDataObjectPropertyEditor.this);
        }
        return panel;
    }
}
