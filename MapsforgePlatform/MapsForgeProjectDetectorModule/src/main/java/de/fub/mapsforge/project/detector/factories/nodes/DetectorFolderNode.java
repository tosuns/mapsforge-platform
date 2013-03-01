/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes;

import de.fub.mapforgeproject.MapsForgeProject;
import de.fub.mapsforge.project.detector.factories.DetectorNodeFactory;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({"CLT_Detector_Folder_Node_Name=Detectors"})
public class DetectorFolderNode extends FilterNode {

    @StaticResource
    private static final String DETECTORS_NODE_ICON_PATH = "de/fub/mapsforge/project/detector/filetype/detector.png";

    public DetectorFolderNode(DataObject aggregationFolder, MapsForgeProject project) {
        super(aggregationFolder.getNodeDelegate(),
                Children.create(new DetectorNodeFactory(aggregationFolder), true),
                new ProxyLookup(
                aggregationFolder.getNodeDelegate().getLookup(),
                Lookups.singleton(project)));
        setDisplayName(Bundle.CLT_Detector_Folder_Node_Name());
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-mapsforge-project/Detector/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(DETECTORS_NODE_ICON_PATH);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
