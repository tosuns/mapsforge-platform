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

import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Serdar
 */
public class ProcessPropertyWrapper extends PropertySupport.ReadWrite<Object> {

    private final Node.Property<Object> property;
    private final ModelSynchronizer.ModelSynchronizerClient client;

    public ProcessPropertyWrapper(ModelSynchronizer.ModelSynchronizerClient client, Node.Property<Object> property) {
        super(property.getName(), property.getValueType(), property.getDisplayName(), property.getShortDescription());
        this.property = property;
        this.client = client;
    }

    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return property.getValue();
    }

    @Override
    public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (property.getValue() == null || property.getValue() != val) {
            property.setValue(val);
            if (client != null) {
                client.modelChangedFromGui();
            }
        }
    }

    @Override
    public boolean canRead() {
        return property.canRead();
    }

    @Override
    public boolean canWrite() {
        return property.canWrite();
    }

    @Override
    public boolean supportsDefaultValue() {
        return property.supportsDefaultValue();
    }

    @Override
    public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
        property.restoreDefaultValue();
    }

    @Override
    public boolean isDefaultValue() {
        return property.isDefaultValue();
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return property.getPropertyEditor();
    }

    @Override
    public boolean isExpert() {
        return property.isExpert();
    }

    @Override
    public void setExpert(boolean expert) {
        property.setExpert(expert);
    }

    @Override
    public boolean isHidden() {
        return property.isHidden();
    }

    @Override
    public void setHidden(boolean hidden) {
        property.setHidden(hidden);
    }

    @Override
    public boolean isPreferred() {
        return property.isPreferred();
    }

    @Override
    public void setPreferred(boolean preferred) {
        property.setPreferred(preferred);
    }

    @Override
    public void setValue(String attributeName, Object value) {
        property.setValue(attributeName, value);
    }

    @Override
    public Object getValue(String attributeName) {
        return property.getValue(attributeName);
    }

    @Override
    public Enumeration<String> attributeNames() {
        return property.attributeNames();
    }

    @Override
    public String toString() {
        return property.toString();
    }
}
