/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories.inference;

import de.fub.maps.project.detector.model.inference.AbstractInferenceModel;
import de.fub.maps.project.detector.model.inference.InferenceMode;
import de.fub.maps.project.detector.model.inference.processhandler.InferenceModelProcessHandler;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class ProcessHandlerNodeFactory extends ChildFactory<InferenceModelProcessHandler> {

    private final AbstractInferenceModel inferenceModel;

    public ProcessHandlerNodeFactory(AbstractInferenceModel inferenceModel) {
        this.inferenceModel = inferenceModel;
    }

    @Override
    protected boolean createKeys(List<InferenceModelProcessHandler> toPopulate) {
        if (inferenceModel != null) {
            toPopulate.add(inferenceModel.getProcessHandlerInstance(InferenceMode.TRAININGS_MODE));
            toPopulate.add(inferenceModel.getProcessHandlerInstance(InferenceMode.CROSS_VALIDATION_MODE));
            toPopulate.add(inferenceModel.getProcessHandlerInstance(InferenceMode.INFERENCE_MODE));
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(InferenceModelProcessHandler processHandler) {
        return new FilterNode(processHandler.getNodeDelegate());
    }
}
