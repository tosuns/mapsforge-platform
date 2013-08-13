/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.actions;

import de.fub.maps.project.MapsProject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Serdar
 */
public class DeleteImpl implements DeleteOperationImplementation {

    private final MapsProject project;

    public DeleteImpl(MapsProject project) {
        this.project = project;
    }

    @Override
    public void notifyDeleting() throws IOException {
    }

    @Override
    public void notifyDeleted() throws IOException {
    }

    @Override
    public List<FileObject> getMetadataFiles() {
        return Arrays.asList(project.getProjectDirectory());
    }

    @Override
    public List<FileObject> getDataFiles() {
        return Arrays.asList(project.getProjectDirectory());
    }
}
