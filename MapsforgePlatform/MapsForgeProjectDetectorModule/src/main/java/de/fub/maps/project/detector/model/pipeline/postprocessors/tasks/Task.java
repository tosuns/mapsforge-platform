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
package de.fub.maps.project.detector.model.pipeline.postprocessors.tasks;

import static de.fub.maps.project.api.process.ProcessState.ERROR;
import static de.fub.maps.project.api.process.ProcessState.INACTIVE;
import static de.fub.maps.project.api.process.ProcessState.RUNNING;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.process.AbstractDetectorProcess;
import de.fub.maps.project.detector.model.process.DetectorProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.node.CustomAbstractnode;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 * Task represent a postprocessor that will be applied by an inferenceModel.
 *
 * @author Serdar
 */
public abstract class Task extends AbstractDetectorProcess<InferenceModelResultDataSet, Void> {

    private InferenceModelResultDataSet resultDataSet;

    public Task() {
    }

    /**
     * Set the Result of the classification via a InferenceModel as input for
     * this task.
     *
     * @param input an InferenceModelResultData instance, null not permitted.
     */
    @Override
    public void setInput(InferenceModelResultDataSet input) {
        this.resultDataSet = input;
    }

    protected InferenceModelResultDataSet getResultDataSet() {
        return resultDataSet;
    }

    /**
     * Task don't have any result.
     *
     * @return null
     */
    @Override
    public Void getResult() {
        return null;
    }

    /**
     * Provides a way to cancel this task process execution. Default this method
     * does nothing. Subclasses should overwrite this mothod, if they want to
     * provide a way to cancel the process.
     *
     * @return default false;
     */
    @Override
    public boolean cancel() {
        return false;
    }

    /**
     * Creates the visual representer of this Task.
     *
     * @return Node instance, null not permitted.
     */
    @Override
    protected Node createNodeDelegate() {
        return new TaskProcessNode(Task.this);
    }

    /**
     * Returns the Icon, which the visuel representer of this task should use.
     * Subclasses can overwrite this method to provide another image
     * representations.
     *
     * @return Image instance
     */
    @Override
    protected Image getDefaultImage() {
        return IconRegister.findRegisteredIcon("processIconNormal.png");
    }

    /**
     * Factory method to get a collection of all via
     * <code>@ServiceProvider</code> registered Task implementations.
     *
     * @return a list of Task instances.
     */
    public static synchronized Collection<Task> findAll() {
        return findAll(Task.class);
    }

    /**
     * Finds and creates the via ProcessDescriptor specified Task
     * implementation, and configures the Task with provided ProcessDescriptor
     * and Detector instance.
     *
     * @param descriptor ProcessDescriptor instance, null not permitted.
     * @param detector Detector instance or null.
     * @return Detector instance
     * @throws
     * de.fub.maps.project.detector.model.process.DetectorProcess.DetectorProcessNotFoundException
     * if the within specified Task implementations could not be found, because
     * it is not registered via the <code>@ServiceProvider</code> annotation.
     */
    public static synchronized Task find(ProcessDescriptor descriptor, Detector detector) throws DetectorProcessNotFoundException {
        assert descriptor != null;
        Task task = find(descriptor.getJavaType(), detector);
        if (task != null) {
            task.setProcessDescriptor(descriptor);
        }
        return task;
    }

    /**
     * Finds or creates the via provided qualified name Task instance.
     *
     * @param qualifiedInstanceName The qualified name of the task, which should
     * be instanciated. null not permitted.
     * @param detector The Detector to which the created Task is associated.
     * @return a Task instance.
     * @throws
     * de.fub.maps.project.detector.model.process.DetectorProcess.DetectorProcessNotFoundException
     * if the specified implementation could not be found, because it's not
     * registered via <code>@ServiceProvider</code> annotations, or
     * instanciated.
     */
    public static synchronized Task find(String qualifiedInstanceName, Detector detector) throws DetectorProcessNotFoundException {
        Task taskProcess = null;
        try {
            Class<?> clazz = null;
            ClassLoader classLoader = Lookup.getDefault().lookup(ClassLoader.class);
            // prefer netbeans classloader
            if (classLoader != null) {
                clazz = classLoader.loadClass(qualifiedInstanceName);
            } else {
                // fall back
                clazz = Class.forName(qualifiedInstanceName);
            }
            if (Task.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                Class<Task> taskProcessClass = (Class<Task>) clazz;
                taskProcess = DetectorProcess.find(taskProcessClass, detector);
            }
        } catch (Throwable ex) {
            throw new DetectorProcessNotFoundException(ex);
        }
        return taskProcess;
    }

    /**
     * The Default implementation of the visual representation of a Task.
     */
    @NbBundle.Messages({"CLT_Tesk_Parameter=Parameters"})
    protected static class TaskProcessNode extends CustomAbstractnode implements PropertyChangeListener, ChangeListener {

        private final Task taskProcess;
        private Sheet.Set set;
        private ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

        public TaskProcessNode(Task taskProcess) {
            this(Children.LEAF, taskProcess);
        }

        public TaskProcessNode(Children children, Task taskProcess) {
            super(children, Lookups.fixed(taskProcess));
            this.taskProcess = taskProcess;
            this.taskProcess.addPropertyChangeListener(WeakListeners.propertyChange(TaskProcessNode.this, this.taskProcess));
            setDisplayName(taskProcess.getName());
            setShortDescription(taskProcess.getDescription());
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            if (taskProcess.getDetector() != null) {
                modelSynchronizerClient = taskProcess.getDetector().create(TaskProcessNode.this);
                set = Sheet.createPropertiesSet();
                set.setDisplayName(Bundle.CLT_Tesk_Parameter());
                sheet.put(set);
                reinitSet();
            }
            return sheet;
        }

        private void reinitSet() {
            List<de.fub.maps.project.detector.model.xmls.Property> propertyList = taskProcess.getProcessDescriptor().getProperties().getPropertyList();
            ProcessProperty property = null;
            for (de.fub.maps.project.detector.model.xmls.Property xmlProperty : propertyList) {
                property = new ProcessProperty(modelSynchronizerClient, xmlProperty);
                set.put(property);
            }
        }

        @Override
        public Image getIcon(int type) {
            Image image = null;
            Image backgroundIcon = null;
            switch (this.taskProcess.getProcessState()) {
                case ERROR:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconError.png");
                    break;
                case SETTING_ERROR:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconError.png");
                    break;
                case INACTIVE:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconNormal.png");
                    break;
                case RUNNING:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconRun.png");
                    break;
                default:
                    throw new AssertionError();
            }

            if (backgroundIcon != null) {
                image = backgroundIcon;
            }

            return image != null ? image : super.getIcon(type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (FilterProcess.PROP_NAME_PROCESS_STATE.equals(evt.getPropertyName())) {
                fireIconChange();
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (modelSynchronizerClient != null) {
                reinitSet();
            }
        }
    }
}
