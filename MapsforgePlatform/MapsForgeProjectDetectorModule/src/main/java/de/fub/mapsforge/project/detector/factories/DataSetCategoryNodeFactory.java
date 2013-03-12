/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.factories.nodes.datasets.InferenceDataSetNode;
import de.fub.mapsforge.project.detector.factories.nodes.datasets.TrainingsDataSetNode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class DataSetCategoryNodeFactory extends ChildFactory<Node> implements ChangeListener {

    private final Detector detector;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public DataSetCategoryNodeFactory(Detector detector) {
        this.detector = detector;
        modelSynchronizerClient = detector.create(DataSetCategoryNodeFactory.this);
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

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
