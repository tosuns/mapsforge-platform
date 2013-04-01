/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.inference;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class FeatureNodeChildFactory extends ChildFactory<FeatureProcess> implements ChangeListener {

    private final Detector detector;
    private ModelSynchronizer.ModelSynchronizerClient msClient;

    public FeatureNodeChildFactory(Detector detector) {
        this.detector = detector;
        if (detector != null) {
            msClient = detector.create(FeatureNodeChildFactory.this);
        }
    }

    @Override
    protected boolean createKeys(List<FeatureProcess> toPopulate) {
        if (detector != null && detector.getInferenceModel() != null) {
            toPopulate.addAll(detector.getInferenceModel().getFeatureList());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(FeatureProcess processDescriptor) {
        return new FilterNode(processDescriptor.getNodeDelegate());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
