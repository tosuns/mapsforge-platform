/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.datasets;

import de.fub.mapsforge.project.detector.factories.DataSetNodeFactory;
import de.fub.mapsforge.project.detector.model.Detector;
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
@NbBundle.Messages({"CLT_InferenceDataSetNode_Name=Inference Dataset",
    "CLT_InferenceDataSetNode_Description=Folder with all GPS-Traces for the preprocessors and inference model for the actual inference process."
})
public class InferenceDataSetNode extends AbstractNode {

    private final Detector detector;

    public InferenceDataSetNode(Detector detector) {
        super(Children.create(
                new DataSetNodeFactory(
                detector, detector.getDetectorDescriptor().getDatasets().getInferenceSet().getDatasetList()), true),
                Lookups.fixed(detector));
        this.detector = detector;
        setDisplayName(Bundle.CLT_InferenceDataSetNode_Name());
        setShortDescription(Bundle.CLT_InferenceDataSetNode_Description());
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
