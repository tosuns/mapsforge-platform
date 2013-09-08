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

import de.fub.gpxmodule.GPXDataObject;
import de.fub.maps.project.MapsAggregatorNodeFactory;
import de.fub.maps.project.MapsProject;
import de.fub.maps.project.aggregator.filetype.AggregatorDataObject;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import de.fub.maps.project.aggregator.xml.Source;
import de.fub.maps.project.utils.AggregatorUtils;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.xml.bind.JAXBException;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class AggregatorWizardWithDatasourcesAction implements ActionListener {

    private final Collection<? extends GPXDataObject> datasources;

    public AggregatorWizardWithDatasourcesAction(Collection<? extends GPXDataObject> allInstances) {
        this.datasources = allInstances;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new AggregatorWizardPanel1());
        panels.add(new AggregatorWizardPanel2());
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
        final DataObject aggregatorFolder = getAggregatorFolder();
        wiz.putProperty(AggregatorWizardAction.PROP_NAME_DATAOBJECT, aggregatorFolder);
        if (aggregatorFolder != null) {

            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                FileUtil.runAtomicAction(new Runnable() {
                    @Override
                    public void run() {
                        Object property = wiz.getProperty(AggregatorWizardAction.PROP_NAME_TEMPLATE);
                        if (property instanceof AggregatorDataObject) {

                            OutputStream outputStream = null;
                            AggregatorDataObject aggregatorDataObject = (AggregatorDataObject) property;
                            String fileName = null;
                            try {
                                AggregatorDescriptor aggregator = aggregatorDataObject.getAggregatorDescriptor();
                                aggregator.setName((String) wiz.getProperty(AggregatorWizardAction.PROP_NAME_NAME));
                                aggregator.setDescription((String) wiz.getProperty(AggregatorWizardAction.PROP_NAME_DESCRIPTION));
                                aggregator.getDatasources().clear();

                                for (GPXDataObject gpxFile : datasources) {

                                    if (gpxFile != null) {
                                        FileObject primaryFile = gpxFile.getPrimaryFile();
                                        addGPXFile(primaryFile, aggregator);
                                    }
                                }

                                fileName = MessageFormat.format("{0}.agg", aggregator.getName());
                                FileObject fileObject = aggregatorFolder.getPrimaryFile().getFileObject(fileName);

                                if (fileObject != null) {
                                    fileName = FileUtil.findFreeFileName(aggregatorFolder.getPrimaryFile(), aggregator.getName(), "agg");
                                }

                                outputStream = aggregatorFolder.getPrimaryFile().createAndOpen(fileName);

                                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(aggregator.getClass());
                                javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                                marshaller.marshal(aggregator, outputStream);

                            } catch (IOException ex) {
                                if (outputStream != null && fileName != null) {
                                    handleExceptionOfOutputStream(outputStream, fileName, aggregatorFolder);
                                }
                            } catch (JAXBException ex) {
                                if (outputStream != null && fileName != null) {
                                    handleExceptionOfOutputStream(outputStream, fileName, aggregatorFolder);
                                }
                            } finally {
                                if (outputStream != null) {
                                    try {
                                        outputStream.close();
                                    } catch (IOException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            }

                        }
                    }
                });
            }
        }
    }

    private void handleExceptionOfOutputStream(OutputStream outputStream, String fileName, DataObject aggregatorFolder) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException ee) {
                Exceptions.printStackTrace(ee);
            }
        }
        FileObject fileObject = aggregatorFolder.getPrimaryFile().getFileObject(fileName);
        if (fileObject != null) {
            try {
                fileObject.delete();
            } catch (IOException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
    }

    private DataObject getAggregatorFolder() {
        if (!datasources.isEmpty()) {
            Project project = AggregatorUtils.findProject(datasources.iterator().next().getPrimaryFile());
            if (project instanceof MapsProject) {
                try {
                    MapsProject mapsProject = (MapsProject) project;
                    String folderPath = mapsProject.getProjectData().getProjectFolders().getFolderPath(MapsAggregatorNodeFactory.AGGREGATION_BUILDER_FILENAME);
                    FileObject fileObject = mapsProject.getProjectDirectory().getFileObject(folderPath);
                    if (fileObject != null) {
                        return DataObject.find(fileObject);
                    }
                } catch (JAXBException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return null;
    }

    private void addGPXFile(FileObject fileObject, AggregatorDescriptor aggregator) {
        if (fileObject.isData()) {
            if (fileObject.getExt() != null && "gpx".equalsIgnoreCase(fileObject.getExt().toLowerCase(Locale.getDefault()))) {
                File file = FileUtil.toFile(fileObject);
                if (file != null) {
                    Source source = new Source();
                    String url = file.getAbsolutePath().replaceAll("\\\\", "/");
                    source.setUrl(url);
                    List<Source> sources = aggregator.getDatasources();
                    if (!sources.contains(source)) {
                        sources.add(source);
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
