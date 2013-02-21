/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories.nodes.properties;

import java.beans.PropertyEditor;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Serdar
 */
public abstract class ClassProperty extends PropertySupport.ReadWrite<ClassWrapper> {

    private PropertyEditor editor = null;
    private final Class<?> interf;

    public ClassProperty(String name, String displayName, String shortDescription, Class<?> interf) {
        super(name, ClassWrapper.class, displayName, shortDescription);
        this.interf = interf;
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        if (editor == null) {
            editor = new InterfacePropertyEditorSupport(interf);
        }
        return editor;
    }
}
