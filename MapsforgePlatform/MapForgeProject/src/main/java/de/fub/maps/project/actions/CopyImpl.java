/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.actions;

import de.fub.maps.project.MapsProject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Serdar
 */
public class CopyImpl implements CopyOperationImplementation {

    private final MapsProject project;

    public CopyImpl(MapsProject project) {
        this.project = project;
    }

    @Override
    public void notifyCopying() throws IOException {
    }

    @Override
    public void notifyCopied(Project original, File originalPath, String nueName) throws IOException {
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
