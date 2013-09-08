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
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
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
public class PreProcessorNodeFactory extends ChildFactory<FilterProcess> implements ChangeListener {

    private final Detector detector;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public PreProcessorNodeFactory(Detector detector) {
        this.detector = detector;
        modelSynchronizerClient = detector.create(PreProcessorNodeFactory.this);
    }

    @Override
    protected boolean createKeys(List<FilterProcess> toPopulate) {
        toPopulate.addAll(detector.getPreProcessorPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(FilterProcess filter) {
        return new FilterNode(filter.getNodeDelegate());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
