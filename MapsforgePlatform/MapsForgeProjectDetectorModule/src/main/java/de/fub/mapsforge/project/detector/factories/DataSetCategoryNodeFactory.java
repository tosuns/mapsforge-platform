/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.factories.nodes.datasets.InferenceDataSetNode;
import de.fub.mapsforge.project.detector.factories.nodes.datasets.TrainingsDataSetNode;
import de.fub.mapsforge.project.detector.model.Detector;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class DataSetCategoryNodeFactory extends ChildFactory<Node> {

    private final Detector detector;

    public DataSetCategoryNodeFactory(Detector detector) {
        this.detector = detector;
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.add(new TrainingsDataSetNode(detector));
        toPopulate.add(new InferenceDataSetNode(detector));
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return node;
    }
}
