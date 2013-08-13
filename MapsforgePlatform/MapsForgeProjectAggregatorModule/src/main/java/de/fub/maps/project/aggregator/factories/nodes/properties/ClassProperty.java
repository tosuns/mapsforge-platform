/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.factories.nodes.properties;

import de.fub.maps.project.aggregator.factories.nodes.properties.InterfacePropertyEditorSupport;
import de.fub.maps.project.aggregator.xml.Property;
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
