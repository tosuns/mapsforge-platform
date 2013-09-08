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
package de.fub.maps.project.aggregator.actions.wizards.aggregator;

import java.text.MessageFormat;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class AggregatorWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AggregatorVisualPanel1 component;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    private WizardDescriptor wiz;

    @Override
    public AggregatorVisualPanel1 getComponent() {
        if (component == null) {
            component = new AggregatorVisualPanel1();
            component.getAggregatorName().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update();
                }

                private void update() {
                    Object property = wiz.getProperty(AggregatorWizardAction.PROP_NAME_DATAOBJECT);
                    if (property instanceof DataObject
                            && component.getAggregatorName().getText() != null) {
                        DataObject dataObject = (DataObject) property;
                        component.getAggregatorLocation().setText(
                                MessageFormat.format("{0}/{1}.agg",
                                        dataObject.getPrimaryFile().getPath(),
                                        component.getAggregatorName().getText().replaceAll(".agg", "")));
                    }
                    changeSupport.fireChange();
                }
            });
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return getComponent().getAggregatorName().getText() != null
                && getComponent().getAggregatorName().getText().length() > 0;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        Object property = wiz.getProperty(AggregatorWizardAction.PROP_NAME_DATAOBJECT);
        if (property instanceof DataObject) {
            DataObject dataObject = (DataObject) property;
            if (getComponent().getAggregatorName().getText() == null) {
                getComponent().getAggregatorLocation().setText(
                        MessageFormat.format("{0}/",
                                dataObject.getPrimaryFile().getPath()));
            } else if (getComponent().getAggregatorName().getText() != null && getComponent().getAggregatorName().getText().length() > 0) {
                getComponent().getAggregatorLocation().setText(
                        MessageFormat.format("{0}/{1}.agg",
                                dataObject.getPrimaryFile().getPath(),
                                getComponent().getAggregatorName().getText()));
            } else {
                getComponent().getAggregatorLocation().setText(
                        MessageFormat.format("{0}/",
                                dataObject.getPrimaryFile().getPath()));
            }
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(AggregatorWizardAction.PROP_NAME_NAME, getComponent().getAggregatorName().getText());
        wiz.putProperty(AggregatorWizardAction.PROP_NAME_DESCRIPTION, getComponent().getAggregatorDescription().getText());
    }
}
