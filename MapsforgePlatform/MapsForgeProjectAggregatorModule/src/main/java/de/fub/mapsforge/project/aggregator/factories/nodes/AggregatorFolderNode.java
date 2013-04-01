/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories.nodes;

import de.fub.mapforgeproject.MapsForgeProject;
import de.fub.mapsforge.project.aggregator.factories.AggregatorNodeFactory;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.filesystems.FileObject;
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
@NbBundle.Messages({"CLT_Aggregator_Folder_Node_Name=Aggregators"})
public class AggregatorFolderNode extends FilterNode {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/aggregationBuilderIcon.png";
    private Image image = null;
    private final DataObject dataObject;

    public AggregatorFolderNode(DataObject aggregationFolder, MapsForgeProject project) {
        super(aggregationFolder.getNodeDelegate(),
                Children.create(new AggregatorNodeFactory(aggregationFolder), true),
                new ProxyLookup(
                aggregationFolder.getNodeDelegate().getLookup(),
                Lookups.singleton(project)));
        this.dataObject = aggregationFolder;
        setDisplayName(Bundle.CLT_Aggregator_Folder_Node_Name());

    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-mapsforge-project/Aggregator/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public Image getIcon(int type) {
        if (image == null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            image = IconRegister.findSystemIcon(fileObject);
            if (image instanceof BufferedImage) {
                BufferedImage bufferedImage = (BufferedImage) image;
                Graphics2D g2d = bufferedImage.createGraphics();
                try {
                    Image iconImage = ImageUtilities.loadImage(ICON_PATH);
                    Image scaledInstance = iconImage.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
                    g2d.drawImage(scaledInstance, 6, 6, null);
                } finally {
                    g2d.dispose();
                }
            }
        }
        return image;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
