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

import de.fub.maps.project.detector.factories.TrainingsDataNodeFactory;
import de.fub.maps.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_TrainingsDataSetNode_Name=Trainings Set",
    "CLT_TrainsingsDataSetNode_Description=Folder with all GPS-Traces for the preprocessors and inference model for trainings propose."})
public class TrainingsDataSetNode extends AbstractNode {

    private final Detector detector;

    public TrainingsDataSetNode(Detector detector) {
        super(Children.create(new TrainingsDataNodeFactory(detector), true), Lookups.fixed(detector));
        this.detector = detector;
        setDisplayName(Bundle.CLT_TrainingsDataSetNode_Name());
        setShortDescription(Bundle.CLT_TrainsingsDataSetNode_Description());
    }

    @Override
    public Image getIcon(int type) {
        Image image = IconRegister.getFolderIcon();
        return image != null ? image : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[0];
    }
}
