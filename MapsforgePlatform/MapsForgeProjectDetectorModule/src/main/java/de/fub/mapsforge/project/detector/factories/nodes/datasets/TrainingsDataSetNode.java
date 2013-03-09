/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.datasets;

import de.fub.mapsforge.project.detector.factories.TrainingsDataNodeFactory;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
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
}
