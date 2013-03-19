/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.inference;

import de.fub.mapsforge.project.detector.factories.nodes.ProcessDescriptionNode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.Features;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
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
public class FeatureNodeChildFactory extends ChildFactory<ProcessDescriptor> implements ChangeListener {

    private final Detector detector;
    private ModelSynchronizer.ModelSynchronizerClient msClient;

    public FeatureNodeChildFactory(Detector detector) {
        this.detector = detector;
        if (detector != null) {
            msClient = detector.create(FeatureNodeChildFactory.this);
        }
    }

    @Override
    protected boolean createKeys(List<ProcessDescriptor> toPopulate) {
        if (detector != null && detector.getInferenceModel() != null) {
            Features features = detector.getInferenceModel().getInferenceModelDescriptor().getFeatures();
            List<ProcessDescriptor> featureList = features.getFeatureList();
            toPopulate.addAll(featureList);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(ProcessDescriptor processDescriptor) {
        return new ProcessDescriptionNode(msClient, processDescriptor);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
