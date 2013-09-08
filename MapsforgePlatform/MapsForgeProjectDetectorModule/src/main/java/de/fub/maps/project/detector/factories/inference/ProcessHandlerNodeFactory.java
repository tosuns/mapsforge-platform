/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
