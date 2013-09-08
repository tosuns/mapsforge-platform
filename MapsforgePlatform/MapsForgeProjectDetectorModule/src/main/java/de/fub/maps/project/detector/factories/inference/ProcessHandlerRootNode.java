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
import de.fub.utilsmodule.node.CustomAbstractnode;
import java.util.concurrent.Callable;
import org.openide.nodes.Children;

/**
 *
 * @author Serdar
 */
public class ProcessHandlerRootNode extends CustomAbstractnode {

    public ProcessHandlerRootNode(final AbstractInferenceModel inferenceModel) {
        super(Children.createLazy(new Callable<Children>() {
            @Override
            public Children call() throws Exception {
                return inferenceModel != null ? Children.create(new ProcessHandlerNodeFactory(inferenceModel), true) : Children.LEAF;
            }
        }));
    }
}
