/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.api.process;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 * Default implementation of the visual representation for a
 * <code>Process</code>.
 *
 * @author Serdar
 */
public class ProcessNode extends AbstractNode implements PropertyChangeListener, ProcessPipeline.ProcessListener {

    private final Process<?, ?> process;

    public ProcessNode(Process<?, ?> process) {
        super(Children.LEAF, Lookups.singleton(process));
        this.process = process;
        setDisplayName(process.getName());
        setShortDescription(process.getDescription());
        process.addProcessListener(ProcessNode.this);
    }

    public Process<?, ?> getProcess() {
        return process;
    }

    @Override
    public String getDisplayName() {
        if (process != null) {
            return process.getName();
        }
        return super.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        if (process != null) {
            return process.getDescription();
        }
        return super.getShortDescription();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireIconChange();
    }

    @Override
    public void changed(ProcessPipeline.ProcessEvent event) {
        fireIconChange();
    }

    @Override
    public void started() {
        fireIconChange();
    }

    @Override
    public void canceled() {
        fireIconChange();
    }

    @Override
    public void finished() {
        fireIconChange();
    }
}
