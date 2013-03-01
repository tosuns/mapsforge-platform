/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.Task;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class PostProcessorNodeFactory extends ChildFactory<Task<?, ?>> {

    private final Detector detector;

    public PostProcessorNodeFactory(Detector detector) {
        this.detector = detector;
    }

    @Override
    protected boolean createKeys(List<Task<?, ?>> toPopulate) {
        toPopulate.addAll(detector.getPostProcessorPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(Task<?, ?> task) {
        return task.getNodeDelegate();
    }
}
