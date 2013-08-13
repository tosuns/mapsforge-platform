/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories.nodes.datasets;

import de.fub.maps.project.detector.factories.DataSetNodeFactory;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.TransportMode;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class TransportModeNode extends AbstractNode {

    private final Detector detector;

    public TransportModeNode(Detector detector, TransportMode transportMode) {
        super(Children.create(new DataSetNodeFactory(detector, transportMode.getDataset()), true), Lookup.EMPTY);
        setDisplayName(transportMode.getName().toLowerCase());
        this.detector = detector;
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
