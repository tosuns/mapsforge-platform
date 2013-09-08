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
package de.fub.maps.project.aggregator.actions.wizards.datasource;

import de.fub.maps.project.MapsProject;
import de.fub.maps.project.aggregator.xml.Source;
import de.fub.maps.project.datasource.MapsDatasourceNodeFactory;
import de.fub.maps.project.models.Aggregator;
import de.fub.maps.project.utils.AggregatorUtils;
import de.fub.maps.project.xml.Maps;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.xml.bind.JAXBException;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

@ActionID(
        category = "Aggregator",
        id = "de.fub.maps.project.aggregator.actions.wizards.DatasourceWizardAction")
@ActionRegistration(
        displayName = "#CTL_AddDatasourcesAction")
@ActionReference(path = "Loaders/text/aggregationbuilder+xml/Actions", position = 225)
@NbBundle.Messages({
    "CTL_AddDatasourcesAction=Add Datasources",
    "# {0} - Foldername",
    "# {1} - projectname",
    "CLT_Datasource_Folder_Not_Found=Couldn't find datasource folder {0} in project {1}",
    "CLT_No_Datasource_Entry_In_Project_Register=Couldn't find any datasource folder registration in project property file!",
    "CLT_Datasource_Dialog_Title=Datasource Dialog"})
public final class DatasourceWizardAction implements ActionListener {

    private final Aggregator context;

    public DatasourceWizardAction(Aggregator context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Project project = AggregatorUtils.findProject(context.getDataObject().getPrimaryFile());
            if (project instanceof MapsProject) {
                MapsProject mapsForgeProject = (MapsProject) project;
                Maps projectData = mapsForgeProject.getProjectData();
                String folderPath = projectData.getProjectFolders().getFolderPath(MapsDatasourceNodeFactory.DATASOURCE_FILENAME);
                FileObject fileObject = null;
                if (folderPath != null) {
                    fileObject = project.getProjectDirectory().getFileObject(folderPath);
                    if (fileObject != null) {
                        DataObject dataObject = DataObject.find(fileObject);
                        invokeWizard(dataObject);
                    } else {
                        throw new FileNotFoundException(Bundle.CLT_Datasource_Folder_Not_Found(folderPath, project.getProjectDirectory()));
                    }
                } else {
                    throw new FileNotFoundException(Bundle.CLT_No_Datasource_Entry_In_Project_Register());
                }
            }
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void invokeWizard(DataObject dataObject) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new DatasourceWizardPanel1());
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
        wiz.setTitle(Bundle.CLT_Datasource_Dialog_Title());
        wiz.putProperty(DatasourceWizardPanel1.DATASOURCE_FILE_OBJECT, dataObject);
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            FileUtil.runAtomicAction(new Runnable() {
                @Override
                public void run() {
                    Object property = wiz.getProperty(DatasourceWizardPanel1.SELECTED_NODES);
                    if (property instanceof Node[]) {
                        Node[] selectedNodes = (Node[]) property;
                        for (int i = 0; i < selectedNodes.length; i++) {
                            Node node = selectedNodes[i];
                            DataObject dObject = node.getLookup().lookup(DataObject.class);
                            if (dObject != null) {
                                FileObject primaryFile = dObject.getPrimaryFile();
                                addGPXFile(primaryFile);
                            }
                        }
                        context.getDataObject().save();
                    }
                }
            });
        }
    }

    private void addGPXFile(FileObject fileObject) {
        if (fileObject.isData()) {
            if (fileObject.getExt() != null && "gpx".equalsIgnoreCase(fileObject.getExt().toLowerCase(Locale.getDefault()))) {
                File file = FileUtil.toFile(fileObject);
                if (file != null) {
                    Source source = new Source();
                    String url = file.getAbsolutePath().replaceAll("\\\\", "/");
                    source.setUrl(url);
                    List<Source> datasources = context.getAggregatorDescriptor().getDatasources();
                    if (!datasources.contains(source)) {
                        datasources.add(source);
                    }
                }
            }
        } else if (fileObject.isFolder()) {
            for (FileObject child : fileObject.getChildren()) {
                addGPXFile(child);
            }
        }
    }
}
