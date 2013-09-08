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
package de.fub.maps.project.datasource.actions;

import de.fub.maps.project.MapsProject;
import de.fub.maps.project.datasource.MapsDatasourceNodeFactory;
import de.fub.maps.project.datasource.service.DataImportService;
import de.fub.maps.project.xml.Maps;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.ServiceProvider;

@ActionID(
        category = "Project",
        id = "de.fub.maps.project.ImportGPX")
@ActionRegistration(
        displayName = "#CTL_ImportGPX", lazy = false)
@ActionReference(path = "Projects/org-maps-project/GPXDataSource/Actions", position = 0)
@Messages({"CTL_ImportGPX=Import GPX Data",
    "CLT_Import_From_Disk=From Disk...",
    "CLT_GPX_File_Import=GPX File Import",
    "CLT_File_Filter_Description=GPX Files",
    "CLT_Folder_Creator_Title=New Folder Name",
    "CLT_Folder_Creator_Message=Please specify a folder name for the to be imported GPX-Files."})
public final class ImportGPXAction extends AbstractAction implements ContextAwareAction, Presenter.Popup {

    private static final long serialVersionUID = 1L;
    private JMenu contextMenu = new JMenu(Bundle.CTL_ImportGPX(), false);
    private transient MapsProject project;
    private final Lookup context;

    public ImportGPXAction() {
        this(Utilities.actionsGlobalContext());
    }

    public ImportGPXAction(Lookup context) {
        assert context != null;
        this.context = context;
        init();
    }

    private void init() {
        project = context.lookup(MapsProject.class);
        contextMenu.setEnabled(project != null);
        putValue(Action.NAME, Bundle.CTL_ImportGPX());
        FileObject gpxDataSourceFolder;
        try {
            if (project != null) {
                gpxDataSourceFolder = getGPXDataSourceFolder();
                if (gpxDataSourceFolder != null) {
                    for (DataImportService service : getImportServiceProviderClasses()) {
                        contextMenu.add(new DelegateAction(service, gpxDataSourceFolder));
                    }
                }
            }
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    private FileObject getGPXDataSourceFolder() throws JAXBException, IOException {
        Maps projectData = project.getProjectData();
        FileObject datasourceFolder = null;
        if (projectData.getProjectFolders() != null) {
            datasourceFolder = project.getProjectDirectory().getFileObject(projectData.getProjectFolders().getFolderPath(MapsDatasourceNodeFactory.DATASOURCE_FILENAME));
        }
        return datasourceFolder;
    }

    private ArrayList<DataImportService> getImportServiceProviderClasses() {
        ArrayList<DataImportService> list = new ArrayList<DataImportService>();
        Result<DataImportService> result = Lookup.getDefault().lookupResult(DataImportService.class);
        for (DataImportService service : result.allInstances()) {
            list.add(service);
        }
        return list;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // Do nothing
    }

    private static NotifyDescriptor.InputLine createFolderNameDialog() {
        return new NotifyDescriptor.InputLine(
                Bundle.CLT_Folder_Creator_Message(),
                Bundle.CLT_Folder_Creator_Title(),
                NotifyDescriptor.InputLine.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return contextMenu;
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new ImportGPXAction(actionContext);
    }

    private static class DelegateAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final DataImportService delegate;
        private final FileObject dataSourceFileObject;

        private DelegateAction(DataImportService delegate, FileObject dataSourceFileObject) {
            assert delegate != null;
            this.delegate = delegate;
            putValue(NAME, delegate.getName());
            this.dataSourceFileObject = dataSourceFileObject;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                DataImportService service = this.delegate.getClass().newInstance();
                service.setDestinationFolder(dataSourceFileObject);
                service.actionPerformed(e);
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @ServiceProvider(service = DataImportService.class)
    public static class ImportFormDiskAction implements DataImportService {

        private FileObject gpxDataSourceFolder;

        public ImportFormDiskAction() {
        }

        @Messages({"CLT_Copy_Files_Start_Message=Copying Files",
            "# {0} - fileName",
            "CLT_Copy_File_Message=Copying File {0}"})
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean repeat = true;
            NotifyDescriptor.InputLine nd = createFolderNameDialog();

            // repeat procedure as long repeat equals true.
            while (repeat) {

                Object notify = DialogDisplayer.getDefault().notify(nd);
                if (NotifyDescriptor.OK_OPTION.equals(notify)) {
                    try {
                        if (gpxDataSourceFolder != null) {
                            final String folderName = nd.getInputText();
                            FileObject gpxFolderFileObject = gpxDataSourceFolder.getFileObject(folderName);

                            // check if folfer name already exists.
                            if (gpxFolderFileObject == null) {
                                gpxFolderFileObject = gpxDataSourceFolder.createFolder(folderName);
                                JFileChooser fileChooser = createGPXFileChooser();
                                int result = fileChooser.showOpenDialog(null);

                                if (JFileChooser.APPROVE_OPTION == result && fileChooser.getSelectedFiles().length > 0) {
                                    // start copy/import procedure.
                                    copyProcedure(fileChooser.getSelectedFiles(), gpxFolderFileObject);
                                }

                                // doesn't matter what the user selects. either way the
                                // import process finished.
                                repeat = false;
                            } else {
                                // create error message, because there already is a folder with the
                                // specified name. repeat process.
                                nd = createErrorDialog(folderName);
                            }
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                        repeat = false;
                    }
                } else {
                    // user aborted import process.
                    repeat = false;
                }
            }
        }

        private JFileChooser createGPXFileChooser() {
            JFileChooser fileChooser = new FileChooserBuilder(ImportGPXAction.class).setTitle(Bundle.CLT_GPX_File_Import()).addFileFilter(new GPXFileFilter()).createFileChooser();

            fileChooser.setMultiSelectionEnabled(true);
            return fileChooser;
        }

        @Messages({"# {0} - folderName",
            "CLT_Folder_Creator_Error_Message=Folder {0} exists already!"})
        private NotifyDescriptor.InputLine createErrorDialog(String folderName) {
            InputLine notifyDescriptor = createFolderNameDialog();
            notifyDescriptor.setInputText(folderName);
            notifyDescriptor.setMessageType(InputLine.ERROR_MESSAGE);
            notifyDescriptor.setMessage(Bundle.CLT_Folder_Creator_Error_Message(folderName));
            return notifyDescriptor;
        }

        private void copyProcedure(File[] selectedFiles, FileObject gpxFolderFileObject) throws IOException {
            ProgressHandle handler = ProgressHandleFactory.createHandle(Bundle.CLT_Copy_Files_Start_Message());
            try {
                handler.switchToDeterminate(selectedFiles.length);
                int i = 0;
                for (File file : selectedFiles) {
                    FileObject sourceFile = FileUtil.toFileObject(file);
                    if (sourceFile != null) {
                        handler.setDisplayName(Bundle.CLT_Copy_File_Message(sourceFile.getNameExt()));
                        FileUtil.copyFile(sourceFile, gpxFolderFileObject, sourceFile.getName());
                    }
                    handler.progress(++i);
                }
            } finally {
                handler.finish();
            }
        }

        @Override
        public void setDestinationFolder(FileObject destinationFoldert) {
            this.gpxDataSourceFolder = destinationFoldert;
        }

        @Override
        public String getName() {
            return Bundle.CLT_Import_From_Disk();
        }

        private static class GPXFileFilter extends FileFilter {

            public GPXFileFilter() {
            }

            @Override
            public boolean accept(File f) {
                FileObject fileObject = FileUtil.toFileObject(f);
                return fileObject != null && "gpx".equalsIgnoreCase(fileObject.getExt());
            }

            @Override
            public String getDescription() {
                return Bundle.CLT_File_Filter_Description();
            }
        }
    }
}
