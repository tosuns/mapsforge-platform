/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Serdar
 */
class MapsForgeProjectInfo implements ProjectInformation {

    @StaticResource
    private static final String PROJECT_ICON = "de/fub/mapforgeproject/icons/mapsforgeIcon16.png";
    private final MapsForgeProject mapForgeProject;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    MapsForgeProjectInfo(MapsForgeProject project) {
        this.mapForgeProject = project;
    }

    @Override
    public String getName() {
        return mapForgeProject.getProjectDirectory().getName();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon(PROJECT_ICON, false);
    }

    @Override
    public Project getProject() {
        return mapForgeProject;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
