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
package de.fub.maps.project.nodes;

import de.fub.maps.project.MapsProject;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
public class MapsProjectNode extends FilterNode implements FileChangeListener {

    private final ProjectInformation projectInfo;

    public MapsProjectNode(Node original, MapsProject project) {
        super(original,
                NodeFactorySupport.createCompositeChildren(project, "Projects/org-maps-project/Nodes"),
                new ProxyLookup(project.getLookup(), original.getLookup()));
        project.getProjectDirectory().addFileChangeListener(FileUtil.weakFileChangeListener(MapsProjectNode.this, project));
        projectInfo = ProjectUtils.getInformation(project);
        assert projectInfo != null;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Loaders/text/mapsproject+xml/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
//        return super.getActions(context);
    }

    @Override
    public String getDisplayName() {
        return projectInfo.getDisplayName();
    }

    @Override
    public String getName() {
        return projectInfo.getName();
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.icon2Image(projectInfo.getIcon());
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        // do nothing
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        // do nothing
    }

    @Override
    public void fileChanged(FileEvent fe) {
        // do nothing
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        // do nothing
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        // do nothing
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
        // do nothing
    }
}
