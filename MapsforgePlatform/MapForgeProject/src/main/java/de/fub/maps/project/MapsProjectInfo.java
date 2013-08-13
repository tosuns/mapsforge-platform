/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project;

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
class MapsProjectInfo implements ProjectInformation {

    @StaticResource
    private static final String PROJECT_ICON = "de/fub/maps/project/icons/mapsforgeIcon16.png";
    private final MapsProject mapForgeProject;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    MapsProjectInfo(MapsProject project) {
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
