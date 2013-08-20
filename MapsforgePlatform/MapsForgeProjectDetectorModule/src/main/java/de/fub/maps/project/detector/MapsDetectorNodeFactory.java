/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector;

import de.fub.maps.project.MapsProject;
import de.fub.maps.project.detector.factories.nodes.DetectorFolderNode;
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
 * Node Factory to provide detector nodes for the MapProject.
 *
 * @author Serdar
 */
@NodeFactory.Registration(projectType = "org-maps-project", position = 2000)
public class MapsDetectorNodeFactory implements NodeFactory {

    public static final String DETECTORS_FILENAME = "Dectectors";

    @Override
    public NodeList<?> createNodes(Project project) {
        MapsProject mapsForgeProject = project.getLookup().lookup(MapsProject.class);
        return new DetectorNodeList(mapsForgeProject);
    }

    private static class DetectorNodeList implements NodeList<Node> {

        private final MapsProject mapsForgeProject;
        private transient final ChangeSupport cs = new ChangeSupport(this);

        public DetectorNodeList(MapsProject mapsForgeProject) {
            this.mapsForgeProject = mapsForgeProject;
        }

        @Override
        public List<Node> keys() {
            List<Node> nodeList = new ArrayList<Node>();
            if (mapsForgeProject != null) {
                try {
                    Maps projectData = mapsForgeProject.getProjectData();

                    // Find GPX Datasource folder
                    if (projectData.getProjectFolders() != null) {

                        String datasourceFolderPath = projectData.getProjectFolders().getFolderPath(DETECTORS_FILENAME);
                        if (datasourceFolderPath == null) {
                            datasourceFolderPath = DETECTORS_FILENAME.replaceAll(" ", "");
                            FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(datasourceFolderPath);
                            if (fileObject == null) {
                                fileObject = mapsForgeProject.getProjectDirectory().createFolder(datasourceFolderPath);
                            }
                            DataObject dataObject = DataObject.find(fileObject);
                            nodeList.add(new DetectorFolderNode(dataObject, mapsForgeProject));
                            projectData.getProjectFolders().putFolder(DETECTORS_FILENAME, datasourceFolderPath);
                            mapsForgeProject.modelChanged(DetectorNodeList.this, projectData);

                        } else {
                            // there is an entry for the gpx datasource file
                            // validate path and get folder if possible
                            FileObject fileObject = mapsForgeProject.getProjectDirectory().getFileObject(datasourceFolderPath);
                            if (fileObject != null) {
                                DataObject dataObject = DataObject.find(fileObject);
                                nodeList.add(new DetectorFolderNode(dataObject, mapsForgeProject));
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
