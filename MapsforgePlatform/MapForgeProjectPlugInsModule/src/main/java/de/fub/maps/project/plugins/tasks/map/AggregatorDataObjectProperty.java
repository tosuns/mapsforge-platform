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

import de.fub.maps.project.detector.model.xmls.Property;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class AggregatorDataObjectProperty extends PropertySupport.ReadWrite<DataObject> implements PropertyChangeListener {

    private final ModelSynchronizer.ModelSynchronizerClient client;
    private final Property property;
    private DataObject value;
    private AggregatorChooserPanel chooserPanel;
    private AggregatorDataObjectPropertyEditor editor;

    public AggregatorDataObjectProperty(ModelSynchronizer.ModelSynchronizerClient client, de.fub.maps.project.detector.model.xmls.Property xmlProperty) {
        super(xmlProperty.getId(), DataObject.class, xmlProperty.getName(), xmlProperty.getDescription());
        this.client = client;
        this.property = xmlProperty;
        init();
    }

    private void init() {
        if (property.getValue() != null) {
            String normalizePath = FileUtil.normalizePath(property.getValue());
            File file = new File(normalizePath);
            FileObject fileObject = null;
            if (file.exists()) {
                fileObject = FileUtil.toFileObject(file);
            } else {
                // the file exists not as a last attempt look into
                // the fsf of netbeans.
                fileObject = FileUtil.getConfigFile(property.getValue());
            }
            if (fileObject != null && fileObject.isValid()) {
                try {
                    value = DataObject.find(fileObject);
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        if (editor == null) {
            editor = new AggregatorDataObjectPropertyEditor();
            Component customEditor = editor.getCustomEditor();
            if (customEditor instanceof AggregatorChooserPanel) {
                chooserPanel = (AggregatorChooserPanel) customEditor;
                chooserPanel.addPropertyChangeListener(WeakListeners.propertyChange(AggregatorDataObjectProperty.this, chooserPanel));
            }
        }
        return editor;
    }

    @Override
    public DataObject getValue() throws IllegalAccessException, InvocationTargetException {
        return value;
    }

    @Override
    public void setValue(DataObject val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (this.value != val) {
            this.value = val;
            if (value != null) {
                property.setValue(value.getPrimaryFile().getPath());
            } else {
                property.setValue(null);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (chooserPanel != null && AggregatorChooserPanel.PROP_NAME_PANEL_CLOSED.equals(evt.getPropertyName())) {
            try {
                setValue(chooserPanel.getSelectedAggregatorDataObject());
                client.modelChangedFromGui();
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
