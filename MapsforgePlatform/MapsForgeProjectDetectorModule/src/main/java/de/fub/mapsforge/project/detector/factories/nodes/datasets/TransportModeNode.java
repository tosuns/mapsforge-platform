/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.datasets;

import de.fub.mapsforge.project.detector.factories.DataSetNodeFactory;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.DataSet;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class TransportModeNode extends AbstractNode {

    private final Detector detector;
    private Image image = null;

    public TransportModeNode(Detector detector, List<DataSet> datasetList) {
        super(Children.create(new DataSetNodeFactory(detector, datasetList), true), Lookup.EMPTY);
        setDisplayName(datasetList.iterator().next().getTransportmode());
        this.detector = detector;
    }

    @Override
    public Image getIcon(int type) {
        if (image == null) {
            image = IconRegister.getFolderIcon();
        }
        return image != null ? image : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
