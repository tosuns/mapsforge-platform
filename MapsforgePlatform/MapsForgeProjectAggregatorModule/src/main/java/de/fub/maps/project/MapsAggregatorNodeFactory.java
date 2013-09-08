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
package de.fub.maps.project;

import de.fub.maps.project.aggregator.factories.nodes.AggregatorFolderNode;
import de.fub.maps.project.xml.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Node factory, which provides aggregator nodes for the MapsProject.
 *
 * @author Serdar
 */
@NodeFactory.Registration(projectType = "org-maps-project", position = 1000)
public class MapsAggregatorNodeFactory implements NodeFactory {

    public static final String AGGREGATION_BUILDER_FILENAME = "Aggregation Builders";

    @Override
    public NodeList<?> createNodes(Project project) {
        MapsProject mapsForgeProject = project.getLookup().lookup(MapsProject.class);
        assert mapsForgeProject != null;
        return new AggregationBuilderNodeList(mapsForgeProject);
    }

    private static class AggregationBuilderNodeList implements NodeList<Node> {

        private final MapsProject mapsForgeProject;
        private final ChangeSupport cs = new ChangeSupport(this);

        private AggregationBuilderNodeList(MapsProject mapsForgeProject) {
            this.mapsForgeProject = mapsForgeProject;
        }

        @Override
        public List<Node> keys() {
            List<Node> nodeList = new ArrayList<Node>();
            try {
                Maps projectData = mapsForgeProject.getProjectData();

                // Find GPX Datasource folder
                if (projectData.getProjectFolders() != null) {

                    String aggregatorFolderPath = projectData.getProjectFolders().getFolderPath(AGGREGATION_BUILDER_FILENAME);
                    if (aggregatorFolderPath == null) {
                        aggregatorFolderPath = AGGREGATION_BUILDER_FILENAME.replaceAll(" ", "");
                        FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(aggregatorFolderPath);
                        if (fileObject == null) {
                            fileObject = mapsForgeProject.getProjectDirectory().createFolder(aggregatorFolderPath);
                        }
                        DataObject dataObject = DataObject.find(fileObject);
                        nodeList.add(MapsAggregatorHelper.getInstance().createAggregatorFolderNode(dataObject, mapsForgeProject));
                        projectData.getProjectFolders().putFolder(AGGREGATION_BUILDER_FILENAME, aggregatorFolderPath);
                        mapsForgeProject.modelChanged(AggregationBuilderNodeList.this, projectData);

                    } else {
                        // there is an entry for the gpx datasource file
                        // validate path and get folder if possible
                        FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(aggregatorFolderPath);
                        if (fileObject != null) {
                            DataObject dataObject = DataObject.find(fileObject);
                            nodeList.add(new AggregatorFolderNode(dataObject, mapsForgeProject));
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
