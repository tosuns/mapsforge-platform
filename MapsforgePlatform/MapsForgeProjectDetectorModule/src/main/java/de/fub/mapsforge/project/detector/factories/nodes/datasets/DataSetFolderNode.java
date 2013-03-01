/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.datasets;

import de.fub.mapsforge.project.detector.factories.DataSetCategoryNodeFactory;
import de.fub.mapsforge.project.detector.model.Detector;
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
