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
