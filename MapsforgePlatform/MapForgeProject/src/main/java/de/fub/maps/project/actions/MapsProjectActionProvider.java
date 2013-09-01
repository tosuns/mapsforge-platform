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
package de.fub.maps.project.actions;

import de.fub.maps.project.MapsProject;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Serdar
 */
public class MapsProjectActionProvider implements ActionProvider {

    private final MapsProject project;
    private RequestProcessor.Task task = null;
    private final static Object MUTEX = new Object();

    public MapsProjectActionProvider(MapsProject project) {
        this.project = project;
    }

    @Override
    public String[] getSupportedActions() {
        return new String[]{
            ActionProvider.COMMAND_COPY,
            ActionProvider.COMMAND_DELETE,
            ActionProvider.COMMAND_MOVE,
            ActionProvider.COMMAND_RENAME};
    }

    @Override
    public void invokeAction(final String command, Lookup context) throws IllegalArgumentException {
        synchronized (MUTEX) {
            task = RequestProcessor.getDefault().create(new Runnable() {
                @Override
                public void run() {
                    if (ActionProvider.COMMAND_DELETE.equals(command)) {
                        DefaultProjectOperations.performDefaultDeleteOperation(project);
                    } else if (ActionProvider.COMMAND_RENAME.equals(command)) {
                        DefaultProjectOperations.performDefaultRenameOperation(project, null);
                    } else if (ActionProvider.COMMAND_MOVE.equals(command)) {
                        DefaultProjectOperations.performDefaultMoveOperation(project);
                    } else if (ActionProvider.COMMAND_COPY.equals(command)) {
                        DefaultProjectOperations.performDefaultCopyOperation(project);
                    }
                }
            });
            task.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(Task task) {
                    MapsProjectActionProvider.this.task.removeTaskListener(this);
                    MapsProjectActionProvider.this.task = null;
                }
            });
            if (task != null) {
                task.schedule(0);
            }
        }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        if (task == null) {
            if (ActionProvider.COMMAND_DELETE.equals(command)) {
                return true;
            } else if (ActionProvider.COMMAND_RENAME.equals(command)) {
                return true;
            } else if (ActionProvider.COMMAND_COPY.equals(command)) {
                return true;
            } else if (ActionProvider.COMMAND_MOVE.equals(command)) {
                return true;
            }

        }
        return false;
    }
}
