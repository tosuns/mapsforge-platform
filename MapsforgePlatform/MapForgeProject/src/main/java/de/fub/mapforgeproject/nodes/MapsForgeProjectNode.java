/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.nodes;

import de.fub.mapforgeproject.MapsForgeProject;
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
public class MapsForgeProjectNode extends FilterNode implements FileChangeListener {

    private final ProjectInformation projectInfo;

    public MapsForgeProjectNode(Node original, MapsForgeProject project) {
        super(original,
                NodeFactorySupport.createCompositeChildren(project, "Projects/org-mapsforge-project/Nodes"),
                new ProxyLookup(project.getLookup(), original.getLookup()));
        project.getProjectDirectory().addFileChangeListener(FileUtil.weakFileChangeListener(MapsForgeProjectNode.this, project));
        projectInfo = ProjectUtils.getInformation(project);
        assert projectInfo != null;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Loaders/text/mapsforgeproject+xml/Actions");
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
