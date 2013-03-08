/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes;

import de.fub.mapforgeproject.api.process.ProcessNode;
import de.fub.mapsforge.project.detector.model.Detector;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.openide.util.Utilities;

/**
 *
 * @author Serdar
 */
public class InferenceModelNode extends ProcessNode {

    public static final String ACTION_PATH = "MapsForge/Detector/inferenceModel/Actions";
    private Image image = null;
    private final Detector detector;

    public InferenceModelNode(Detector detector) {
        super(detector.getInferenceModel());
        this.detector = detector;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath(ACTION_PATH);
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public Image getIcon(int type) {
        if (image == null) {
            image = detector.getInferenceModel().getIcon();
        }
        return image != null ? image : super.getIcon(type);
    }
}
