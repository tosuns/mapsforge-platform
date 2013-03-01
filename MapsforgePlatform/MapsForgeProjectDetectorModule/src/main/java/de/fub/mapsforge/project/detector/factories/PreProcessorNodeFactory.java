/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class PreProcessorNodeFactory extends ChildFactory<FilterProcess<?, ?>> {

    private final Detector detector;

    public PreProcessorNodeFactory(Detector detector) {
        this.detector = detector;
    }

    @Override
    protected boolean createKeys(List<FilterProcess<?, ?>> toPopulate) {
        toPopulate.addAll(detector.getPreProcessorPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(FilterProcess<?, ?> filter) {
        return filter.getNodeDelegate();
    }
}
