/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
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
public class PreProcessorNodeFactory extends ChildFactory<FilterProcess<?, ?>> implements ChangeListener {

    private final Detector detector;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public PreProcessorNodeFactory(Detector detector) {
        this.detector = detector;
        modelSynchronizerClient = detector.create(PreProcessorNodeFactory.this);
    }

    @Override
    protected boolean createKeys(List<FilterProcess<?, ?>> toPopulate) {
        toPopulate.addAll(detector.getPreProcessorPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(FilterProcess<?, ?> filter) {
        return new FilterNode(filter.getNodeDelegate());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
