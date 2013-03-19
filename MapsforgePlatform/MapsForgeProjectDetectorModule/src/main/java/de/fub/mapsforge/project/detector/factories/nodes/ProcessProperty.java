/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes;

import de.fub.mapsforge.project.detector.model.xmls.Property;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Serdar
 */
public class ProcessProperty extends PropertySupport.ReadWrite<String> {

    private final ModelSynchronizer.ModelSynchronizerClient clientSynchronizer;
    private final Property property;

    @SuppressWarnings("unchecked")
    public ProcessProperty(ModelSynchronizer.ModelSynchronizerClient clientSynchronizer, Property property) {
        super(property.getName(), String.class, property.getName(), property.getDescription());
        this.clientSynchronizer = clientSynchronizer;
        this.property = property;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return property.getValue();
    }

    @Override
    public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (val != null && !val.equals(property.getValue())) {
            property.setValue(String.valueOf(val));
            clientSynchronizer.modelChanged();
        } else {
            property.setValue(null);
            clientSynchronizer.modelChanged();
        }
    }
}
