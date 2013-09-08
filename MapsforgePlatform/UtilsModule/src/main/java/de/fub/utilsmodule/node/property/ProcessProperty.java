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
