/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.factories.nodes;

import de.fub.maps.project.MapsProject;
import de.fub.maps.project.detector.factories.DetectorNodeFactory;
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
@NbBundle.Messages({"CLT_Detector_Folder_Node_Name=Detectors"})
public class DetectorFolderNode extends FilterNode {

    @StaticResource
    private static final String DETECTORS_NODE_ICON_PATH = "de/fub/maps/project/detector/filetype/detector.png";
    private static Image iconImage = null;
    private final DataObject dataObject;

    public DetectorFolderNode(DataObject detectorFolder, MapsProject project) {
        super(detectorFolder.getNodeDelegate(),
                Children.create(new DetectorNodeFactory(detectorFolder), true),
                new ProxyLookup(
                        detectorFolder.getNodeDelegate().getLookup(),
                        Lookups.singleton(project)));
        this.dataObject = detectorFolder;
        setDisplayName(Bundle.CLT_Detector_Folder_Node_Name());
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-maps-project/Detector/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public Image getIcon(int type) {
        if (iconImage == null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            iconImage = IconRegister.findSystemIcon(fileObject);

            if (iconImage instanceof BufferedImage) {
                BufferedImage bufferedImage = (BufferedImage) iconImage;
                Graphics2D g2d = bufferedImage.createGraphics();
                try {
                    Image detector = ImageUtilities.loadImage(DETECTORS_NODE_ICON_PATH);
                    Image scaledInstance = detector.getScaledInstance(10, 10, Image.SCALE_REPLICATE);
                    g2d.drawImage(scaledInstance, 6, 6, null);
                } finally {
                    g2d.dispose();
                }
            }
        }
        return iconImage;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
