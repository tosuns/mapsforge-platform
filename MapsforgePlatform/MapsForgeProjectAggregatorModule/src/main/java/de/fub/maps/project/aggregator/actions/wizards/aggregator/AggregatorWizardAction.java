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

import de.fub.maps.project.aggregator.filetype.AggregatorDataObject;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import de.fub.maps.project.aggregator.xml.Source;
import de.fub.maps.project.utils.AggregatorUtils;
import de.fub.utilsmodule.xml.jax.JAXBUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.xml.bind.JAXBException;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
// @ActionID(category="...", id="de.fub.mapsforge.project.aggregator.actions.wizards.aggregator.AggregatorWizardAction")
// @ActionRegistration(displayName="Open Aggregator Wizard")
// @ActionReference(path="Menu/Tools", position=...)
public final class AggregatorWizardAction implements ActionListener {

    public static final String PROP_NAME_DATAOBJECT = "Aggregator.dataobject";
    public static final String PROP_NAME_NAME = "Aggregator.name";
    public static final String PROP_NAME_DESCRIPTION = "Aggregator.description";
    public static final String PROP_NAME_DATASOURCES = "Aggregator.datasources";
    public static final String PROP_NAME_TEMPLATE = "Aggregator.template";
    private final DataObject dataObject;

    public AggregatorWizardAction(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @NbBundle.Messages("CLT_New_Aggregator=New Aggregator")
    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new AggregatorWizardPanel1());
        panels.add(new AggregatorWizardPanel2());
        panels.add(new AggregatorWizardPanel3());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        final WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(Bundle.CLT_Aggregator_Name());
        wiz.putProperty(PROP_NAME_DATAOBJECT, dataObject);
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {

            FileUtil.runAtomicAction(new Runnable() {
                @Override
                public void run() {
                    Object property = wiz.getProperty(AggregatorWizardAction.PROP_NAME_TEMPLATE);
                    if (property instanceof AggregatorDataObject) {

                        FileObject destFileObject = null;
                        AggregatorDataObject aggregatorDataObject = (AggregatorDataObject) property;
                        String fileName = null;
                        try {
                            AggregatorDescriptor aggregator = AggregatorUtils.getAggregatorDescritpor(aggregatorDataObject);
                            aggregator.setName((String) wiz.getProperty(AggregatorWizardAction.PROP_NAME_NAME));
                            aggregator.setDescription((String) wiz.getProperty(AggregatorWizardAction.PROP_NAME_DESCRIPTION));
                            aggregator.setCacheFolderPath(null);
                            if (aggregator.getDatasources() != null) {
                                aggregator.getDatasources().clear();
                            }
                            property = wiz.getProperty(AggregatorWizardAction.PROP_NAME_DATASOURCES);

                            if (property instanceof Node[]) {
                                Node[] selectedNodes = (Node[]) property;
                                for (int i = 0; i < selectedNodes.length; i++) {
                                    Node node = selectedNodes[i];
                                    DataObject dObject = node.getLookup().lookup(DataObject.class);
                                    if (dObject != null) {
                                        FileObject primaryFile = dObject.getPrimaryFile();
                                        addGPXFile(primaryFile, aggregator);
                                    }
                                }
                            }

                            fileName = MessageFormat.format("{0}.agg", aggregator.getName());
                            FileObject fileObject = dataObject.getPrimaryFile().getFileObject(fileName);

                            if (fileObject != null) {
                                fileName = FileUtil.findFreeFileName(dataObject.getPrimaryFile(), aggregator.getName(), "agg");
                            }

                            destFileObject = dataObject.getPrimaryFile().createData(fileName);
                            JAXBUtil.saveDetector(destFileObject, aggregator);
                        } catch (IOException ex) {
                            if (destFileObject != null && fileName != null) {
                                handleExceptionOfOutputStream(destFileObject, fileName);
                            }
                        } catch (JAXBException ex) {
                            if (destFileObject != null && fileName != null) {
                                handleExceptionOfOutputStream(destFileObject, fileName);
                            }
                        }
                    }
                }
            });
        }
    }

    private void handleExceptionOfOutputStream(FileObject fileObject, String fileName) {
        if (fileObject != null) {
            try {
                fileObject.delete();
            } catch (IOException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
    }

    private void addGPXFile(FileObject fileObject, AggregatorDescriptor aggregator) {
        if (fileObject.isData()) {
            if ("gpx".equalsIgnoreCase(fileObject.getExt())) {
                File file = FileUtil.toFile(fileObject);
                if (file != null) {
                    Source source = new Source();
                    String url = file.getAbsolutePath().replaceAll("\\\\", "/");
                    source.setUrl(url);
                    List<Source> datasources = aggregator.getDatasources();
                    if (!datasources.contains(source)) {
                        datasources.add(source);
                    }
                }
            }
        } else if (fileObject.isFolder()) {
            for (FileObject child : fileObject.getChildren()) {
                addGPXFile(child, aggregator);
            }
        }
    }
}
