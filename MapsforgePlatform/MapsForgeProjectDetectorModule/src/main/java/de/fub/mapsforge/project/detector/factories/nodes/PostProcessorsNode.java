/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes;

import de.fub.mapsforge.project.detector.factories.PostProcessorNodeFactory;
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
    "CLT_Post_Processor_Node_Name=Post Processors",
    "CLT_Post_Processor_Node_Description=Node of post processing tasks."})
public class PostProcessorsNode extends AbstractNode {

    private Image image = null;
    private final Detector detector;

    public PostProcessorsNode(Detector detector) {
        super(Children.create(new PostProcessorNodeFactory(detector), true), Lookups.fixed(detector));
        setDisplayName(Bundle.CLT_Post_Processor_Node_Name());
        setShortDescription(Bundle.CLT_Post_Processor_Node_Description());
        this.detector = detector;
    }

    @Override
    public Image getIcon(int type) {
        if (image == null) {
            image = IconRegister.findRegisteredIcon("processFolderIcon.png");
        }
        return image != null ? image : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
