/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project;

import de.fub.maps.project.nodes.MapsProjectNode;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
class MapsLogicalView implements LogicalViewProvider {

    private final MapsProject project;

    public MapsLogicalView(MapsProject project) {
        assert project != null;
        this.project = project;
    }

    @Override
    public Node createLogicalView() {
        Node node = null;
        try {
            //Obtain the project directory's node:
            FileObject projectDirectory = project.getProjectDirectory();
            DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
            Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
            //Decorate the project directory's node:
            node = new MapsProjectNode(nodeOfProjectFolder, project);
        } catch (IllegalArgumentException donfe) {
            Exceptions.printStackTrace(donfe);
            //Fallback-the directory couldn't be created -
            //read-only filesystem or something evil happened
            node = new AbstractNode(Children.LEAF);
        }
        return node;
    }

    @Override
    public Node findPath(Node root, Object target) {
        // TODO implement
        return null;
    }
}
