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
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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
