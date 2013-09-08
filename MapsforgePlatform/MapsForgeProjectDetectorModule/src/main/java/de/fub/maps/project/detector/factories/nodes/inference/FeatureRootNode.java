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
package de.fub.maps.project.detector.factories.nodes.inference;

import de.fub.maps.project.detector.factories.inference.FeatureNodeChildFactory;
import de.fub.maps.project.detector.model.Detector;
import de.fub.utilsmodule.node.CustomAbstractnode;
import java.awt.Image;
import java.util.concurrent.Callable;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Feature_Root_Node_Name=Features",
    "CLT_Feature_Root_Node_Description=All Features that this inference model currently contains."
})
public class FeatureRootNode extends CustomAbstractnode {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/detector/model/inference/features/featureIcon.png";

    public FeatureRootNode(final Detector detector) {
        super(Children.createLazy(new Callable<Children>() {
            @Override
            public Children call() throws Exception {
                return detector != null ? Children.create(new FeatureNodeChildFactory(detector), true) : Children.LEAF;
            }
        }));
        setDisplayName(Bundle.CLT_Feature_Root_Node_Name());
        setShortDescription(Bundle.CLT_Feature_Root_Node_Description());
    }

    @Override
    public Image getIcon(int type) {
        Image image = ImageUtilities.loadImage(ICON_PATH);
        if (image == null) {
            image = super.getIcon(type);
        }
        return image;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
