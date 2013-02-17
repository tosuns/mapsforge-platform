/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.actions;

import de.fub.mapforgeproject.MapsForgeProject;
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
public class MapsForgeProjectActionProvider implements ActionProvider {

    private final MapsForgeProject project;
    private RequestProcessor.Task task = null;
    private final static Object MUTEX = new Object();

    public MapsForgeProjectActionProvider(MapsForgeProject project) {
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
                    MapsForgeProjectActionProvider.this.task.removeTaskListener(this);
                    MapsForgeProjectActionProvider.this.task = null;
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
