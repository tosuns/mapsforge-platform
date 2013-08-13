/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = ProjectFactory.class)
public class MapsProjectFactory implements ProjectFactory {

    static final String MAPS_PROJECT_FILE = "mapsforge.xml";

    @Override
    public boolean isProject(FileObject projectDirectory) {
        return projectDirectory.getFileObject(MAPS_PROJECT_FILE) != null;
    }

    @Override
    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        return isProject(projectDirectory) ? new MapsProject(projectDirectory, state) : null;

    }

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {
        //TODO leave unimplemented for the moment
    }
}
