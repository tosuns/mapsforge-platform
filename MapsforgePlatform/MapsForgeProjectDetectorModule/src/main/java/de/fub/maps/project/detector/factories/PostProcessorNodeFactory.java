/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.pipeline.postprocessors.tasks.Task;
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
public class PostProcessorNodeFactory extends ChildFactory<Task> implements ChangeListener {

    private final Detector detector;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public PostProcessorNodeFactory(Detector detector) {
        this.detector = detector;
        modelSynchronizerClient = detector.create(PostProcessorNodeFactory.this);
    }

    @Override
    protected boolean createKeys(List<Task> toPopulate) {
        toPopulate.addAll(detector.getPostProcessorPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(Task task) {
        return new FilterNode(task.getNodeDelegate());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
