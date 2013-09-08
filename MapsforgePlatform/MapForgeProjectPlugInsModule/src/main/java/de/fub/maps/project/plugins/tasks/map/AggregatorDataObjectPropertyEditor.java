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
package de.fub.maps.project.plugins.tasks.map;

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
