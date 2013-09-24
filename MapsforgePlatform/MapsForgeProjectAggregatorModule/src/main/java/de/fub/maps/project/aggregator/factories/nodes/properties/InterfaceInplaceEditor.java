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
package de.fub.maps.project.aggregator.factories.nodes.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class InterfaceInplaceEditor implements InplaceEditor {

    private final JComboBox<ClassWrapper> comboBox = new JComboBox<ClassWrapper>();
    private PropertyEditor editor = null;
    private PropertyModel model;
    private final Class<?> clazz;
    private final Set<ActionListener> actionListenerSet = Collections.synchronizedSet(new HashSet<ActionListener>());

    public InterfaceInplaceEditor(Class<?> clazz) {
        assert clazz != null && clazz.isInterface();
        this.clazz = clazz;

        comboBox.removeAllItems();
        Lookup.Result<?> lookupResult = Lookup.getDefault().lookupResult(this.clazz);

        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        for (Class<?> c : lookupResult.allClasses()) {
            list.add(c);
        }

        Collections.sort(list, new ClassComparator());

        for (Class<?> c : list) {
            comboBox.addItem(new ClassWrapper(c));
        }
        if (comboBox.getItemCount() > - 1) {
            comboBox.setSelectedIndex(0);
        }

        comboBox.setForeground(Color.BLACK);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ActionListener listener : actionListenerSet) {
                    listener.actionPerformed(e);
                }
            }
        });
    }

    JComboBox<ClassWrapper> getComboBox() {
        return comboBox;
    }

    @Override
    public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
        editor = propertyEditor;
        reset();
    }

    @Override
    public JComponent getComponent() {
        return comboBox;
    }

    @Override
    public void clear() {
        editor = null;
        model = null;
    }

    @Override
    public Object getValue() {
        //        comboBox.repaint();
        //        comboBox.updateUI();
        //        ((JComponent) comboBox.getParent()).requestFocus();
        Object selectedItem = comboBox.getSelectedItem();
        return selectedItem;
    }

    @Override
    public void setValue(Object object) {
        comboBox.setSelectedItem(object);
        comboBox.repaint();
        editor.setValue(comboBox.getSelectedItem());
        ((JComponent) comboBox.getParent()).requestFocus();
    }

    @Override
    public boolean supportsTextEntry() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reset() {

        Object value = editor.getValue();
        if (value instanceof ClassWrapper) {
            comboBox.setSelectedItem(value);
        }
        comboBox.repaint();
    }

    @Override
    public void addActionListener(ActionListener al) {
        actionListenerSet.add(al);
    }

    @Override
    public void removeActionListener(ActionListener al) {
        actionListenerSet.remove(al);
    }

    @Override
    public KeyStroke[] getKeyStrokes() {
        return new KeyStroke[0];
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return editor;
    }

    @Override
    public PropertyModel getPropertyModel() {
        return model;
    }

    @Override
    public void setPropertyModel(PropertyModel pm) {
        this.model = pm;
    }

    @Override
    public boolean isKnownComponent(Component component) {
        return component == comboBox || comboBox.isAncestorOf(component);
    }

    private static class ClassComparator implements Comparator<Class<?>>, Serializable {

        private static final long serialVersionUID = 1L;

        public ClassComparator() {
        }

        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            return o1.getSimpleName().compareTo(o2.getSimpleName());
        }
    }
}
