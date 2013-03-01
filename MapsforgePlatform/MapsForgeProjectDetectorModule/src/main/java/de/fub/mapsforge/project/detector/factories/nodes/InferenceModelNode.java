/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes;

import de.fub.mapforgeproject.api.process.ProcessNode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;

/**
 *
 * @author Serdar
 */
public class InferenceModelNode extends ProcessNode {

    private Image image = null;
    private final Detector detector;

    public InferenceModelNode(Detector detector) {
        super(detector.getInferenceModel());
        this.detector = detector;
    }

    @Override
    public Image getIcon(int type) {
        if (image == null) {
            image = IconRegister.findRegisteredIcon("inferenceModelIcon.png");
        }
        return image != null ? image : super.getIcon(type);
    }
}
