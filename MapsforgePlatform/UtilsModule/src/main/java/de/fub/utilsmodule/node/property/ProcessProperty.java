/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.node.property;

import de.fub.utilsmodule.beans.PropertyDescriptor;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Serdar
 */
public class ProcessProperty extends NodeProperty {

    private final ModelSynchronizer.ModelSynchronizerClient clientSynchronizer;

    @SuppressWarnings("unchecked")
    public ProcessProperty(ModelSynchronizer.ModelSynchronizerClient clientSynchronizer, PropertyDescriptor property) {
        super(property);
        this.clientSynchronizer = clientSynchronizer;
    }

    @Override
    public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object value = getValue();
        super.setValue(val);
        if (val != null && !val.equals(value)) {
            notifyModel();
        } else {
            notifyModel();
        }
    }

    private void notifyModel() {
        if (clientSynchronizer != null) {
            clientSynchronizer.modelChangedFromGui();
        }
    }
}
