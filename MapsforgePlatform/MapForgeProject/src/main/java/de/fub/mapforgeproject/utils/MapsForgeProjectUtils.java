/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class MapsForgeProjectUtils {

    private static final Logger LOG = Logger.getLogger(MapsForgeProjectUtils.class.getName());

    public static synchronized Project findProject(FileObject fileObject) {
        Project project = null;

        while (project == null && !fileObject.isRoot()) {
            try {
                project = ProjectManager.getDefault().findProject(fileObject);
            } catch (IllegalArgumentException ex) {
                LOG.log(Level.FINE, ex.getMessage(), ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            fileObject = fileObject.getParent();
        }

        return project;
    }
}
