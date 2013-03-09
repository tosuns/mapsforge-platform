/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.factories.nodes.InferenceModelNode;
import de.fub.mapsforge.project.detector.factories.nodes.PostProcessorsNode;
import de.fub.mapsforge.project.detector.factories.nodes.PreProcessorsNode;
import de.fub.mapsforge.project.detector.factories.nodes.datasets.DataSetFolderNode;
import de.fub.mapsforge.project.detector.model.Detector;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class DetectorSubNodeFactory extends ChildFactory<Node> {

    private final Detector detector;

    public DetectorSubNodeFactory(Detector detector) {
        this.detector = detector;
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.add(new DataSetFolderNode(detector));
        toPopulate.add(new PreProcessorsNode(detector));
        if (detector.getInferenceModel() != null) {
            toPopulate.add(new InferenceModelNode(detector.getInferenceModel()));
        }
        toPopulate.add(new PostProcessorsNode(detector));
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return node;
    }
}
