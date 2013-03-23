/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource;

import de.fub.mapforgeproject.MapsForgeProject;
import de.fub.mapforgeproject.xml.MapsForge;
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
 *
 * @author Serdar
 */
@NodeFactory.Registration(projectType = "org-mapsforge-project", position = 0)
public class MapsForgeDatasourceNodeFactory implements NodeFactory {

    public static final String DATASOURCE_FILENAME = "GPX Datasource";
    public static GPXDatasourceNode gpxDatasourceNode;

    @Override
    public NodeList<?> createNodes(Project project) {
        MapsForgeProject mapsForgeProject = project.getLookup().lookup(MapsForgeProject.class);
        assert mapsForgeProject != null;
        return new MapsForgeDatasourceNodeList(mapsForgeProject);
    }

    private static class MapsForgeDatasourceNodeList implements NodeList<Node> {

        private final MapsForgeProject mapsForgeProject;
        private final ChangeSupport cs = new ChangeSupport(this);

        private MapsForgeDatasourceNodeList(MapsForgeProject mapsForgeProject) {
            this.mapsForgeProject = mapsForgeProject;
        }

        @Override
        public List<Node> keys() {
            List<Node> nodeList = new ArrayList<Node>();
            try {
                MapsForge projectData = mapsForgeProject.getProjectData();

                // Find GPX Datasource folder
                if (projectData.getProjectFolders() != null) {

                    String datasourceFolderPath = projectData.getProjectFolders().getFolderPath(DATASOURCE_FILENAME);
                    if (datasourceFolderPath == null) {
                        datasourceFolderPath = DATASOURCE_FILENAME.replaceAll(" ", "");
                        FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(datasourceFolderPath);
                        if (fileObject == null) {
                            fileObject = mapsForgeProject.getProjectDirectory().createFolder(datasourceFolderPath);
                        }
                        DataObject dataObject = DataObject.find(fileObject);
                        gpxDatasourceNode = new GPXDatasourceNode(dataObject, mapsForgeProject);
                        nodeList.add(gpxDatasourceNode);
                        projectData.getProjectFolders().putFolder(DATASOURCE_FILENAME, datasourceFolderPath);
                        mapsForgeProject.modelChanged(MapsForgeDatasourceNodeList.this, projectData);

                    } else {
                        // there is an entry for the gpx datasource file
                        // validate path and get folder if possible
                        FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(datasourceFolderPath);
                        if (fileObject != null) {
                            DataObject dataObject = DataObject.find(fileObject);
                            gpxDatasourceNode = new GPXDatasourceNode(dataObject, mapsForgeProject);
                            nodeList.add(gpxDatasourceNode);
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
