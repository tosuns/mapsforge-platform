/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes;

import de.fub.mapsforge.project.detector.factories.PreProcessorNodeFactory;
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
@NbBundle.Messages({"CLT_Pre_Processors_Node_Name=Pre Processors", "CLT_Pre_Processors_Node_Description=List or pre processing filters."})
public class PreProcessorsNode extends AbstractNode {

    private Image image = null;
    private final Detector detector;

    public PreProcessorsNode(Detector detector) {
        super(Children.create(new PreProcessorNodeFactory(detector), true), Lookups.fixed(detector));
        this.detector = detector;
        setDisplayName(Bundle.CLT_Pre_Processors_Node_Name());
        setShortDescription(Bundle.CLT_Pre_Processors_Node_Description());
    }

    @Override
    public Image getIcon(int type) {
        if (image == null) {
            image = IconRegister.findRegisteredIcon("filterIcon16.png");
        }
        return image != null ? image : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
