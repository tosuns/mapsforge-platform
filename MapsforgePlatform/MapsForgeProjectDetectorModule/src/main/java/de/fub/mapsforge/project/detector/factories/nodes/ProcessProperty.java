/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes;

import de.fub.mapsforge.project.detector.model.xmls.Property;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class ProcessProperty extends PropertySupport.ReadWrite<Object> {

    private final ModelSynchronizer.ModelSynchronizerClient clientSynchronizer;
    private final Property property;
    private Object value = null;

    @SuppressWarnings("unchecked")
    public ProcessProperty(ModelSynchronizer.ModelSynchronizerClient clientSynchronizer, Property property) {
        super(property.getName(), getClassOf(property), property.getName(), property.getDescription());
        this.clientSynchronizer = clientSynchronizer;
        this.property = property;
        initValue();
    }

    private void initValue() {
        try {
            value = DetectorUtils.getValue(Class.forName(property.getJavaType()), property);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return value;
    }

    @Override
    public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        value = val;
        if (val != null && !val.equals(property.getValue())) {
            property.setValue(String.valueOf(val));
            notifyModel();
        } else {
            property.setValue(null);
            notifyModel();
        }
    }

    private void notifyModel() {
        if (clientSynchronizer != null) {
            clientSynchronizer.modelChangedFromGui();
        }
    }

    private static Class getClassOf(Property property) {
        Class<?> clazz = Object.class;
        try {
            clazz = Class.forName(property.getJavaType());
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return clazz;
    }
}
