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
package de.fub.maps.project.datasource;

import de.fub.maps.project.MapsProject;
import de.fub.maps.project.xml.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Serdar
 */
@NodeFactory.Registration(projectType = "org-maps-project", position = 0)
public class MapsDatasourceNodeFactory implements NodeFactory {

    public static final String DATASOURCE_FILENAME = "GPX Datasource";
    public static GPXDatasourceNode gpxDatasourceNode;

    @Override
    public NodeList<?> createNodes(Project project) {
        MapsProject mapsForgeProject = project.getLookup().lookup(MapsProject.class);
        assert mapsForgeProject != null;
        return new MapsForgeDatasourceNodeList(mapsForgeProject);
    }

    private static class MapsForgeDatasourceNodeList implements NodeList<Node> {

        private final MapsProject mapsForgeProject;
        private final ChangeSupport cs = new ChangeSupport(this);

        private MapsForgeDatasourceNodeList(MapsProject mapsForgeProject) {
            this.mapsForgeProject = mapsForgeProject;
        }

        @Override
        public List<Node> keys() {
            List<Node> nodeList = new ArrayList<Node>();
            try {
                Maps projectData = mapsForgeProject.getProjectData();

                // Find GPX Datasource folder
                if (projectData.getProjectFolders() != null) {

                    String datasourceFolderPath = projectData.getProjectFolders().getFolderPath(DATASOURCE_FILENAME);
                    if (datasourceFolderPath == null) {
                        datasourceFolderPath = DATASOURCE_FILENAME.replaceAll(" ", "");
                        FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(datasourceFolderPath);
                        if (fileObject == null) {
                            fileObject = mapsForgeProject.getProjectDirectory().createFolder(datasourceFolderPath);
                        }
                        nodeList.add(createRootNode(DataObject.find(fileObject)));
                        projectData.getProjectFolders().putFolder(DATASOURCE_FILENAME, datasourceFolderPath);
                        mapsForgeProject.modelChanged(MapsForgeDatasourceNodeList.this, projectData);
                    } else {
                        // there is an entry for the gpx datasource file
                        // validate path and get folder if possible
                        FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(datasourceFolderPath);
                        if (fileObject != null) {
                            nodeList.add(createRootNode(DataObject.find(fileObject)));
                        } else {
                            // TODO implement
                            // inconsitency! signal error message to project node
                        }
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (JAXBException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            return nodeList;
        }

        private Node createRootNode(DataObject dataObject) {
            Preferences preferences = NbPreferences.forModule(MapsDatasourceNodeFactory.class);
            preferences.put(DATASOURCE_FILENAME, dataObject.getPrimaryFile().getPath());
            gpxDatasourceNode = new GPXDatasourceNode(dataObject, mapsForgeProject);
            return gpxDatasourceNode;
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
            cs.fireChange();
        }

        @Override
        public void removeNotify() {
            cs.fireChange();
        }
    }
}
