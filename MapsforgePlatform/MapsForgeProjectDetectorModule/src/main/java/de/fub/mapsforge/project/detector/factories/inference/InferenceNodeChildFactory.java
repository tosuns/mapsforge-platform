/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.inference;

import de.fub.mapsforge.project.detector.factories.nodes.inference.FeatureRootNode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class InferenceNodeChildFactory extends ChildFactory<Node> {

    private final Detector detector;

    public InferenceNodeChildFactory(Detector detector) {
        this.detector = detector;;
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        if (detector != null) {
            toPopulate.add(new FeatureRootNode(detector));
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return new FilterNode(node);
    }
}
