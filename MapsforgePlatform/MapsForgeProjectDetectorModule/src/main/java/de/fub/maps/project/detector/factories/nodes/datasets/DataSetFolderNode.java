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
package de.fub.maps.project.detector.factories.nodes.datasets;

import de.fub.maps.project.detector.factories.DataSetCategoryNodeFactory;
import de.fub.maps.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages("CLT_DataSetsNode_Name=Data Sets")
public class DataSetFolderNode extends AbstractNode {

    private Image image = null;

    public DataSetFolderNode(Detector detector) {
        super(Children.create(new DataSetCategoryNodeFactory(detector), true), Lookup.EMPTY);
        setDisplayName(Bundle.CLT_DataSetsNode_Name());
    }

    @Override
    public Image getIcon(int type) {
        if (image == null) {
            image = IconRegister.findRegisteredIcon("datasetIcon.png");
        }
        return image != null ? image : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
