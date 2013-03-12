/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.postprocessors;

import de.fub.mapforgeproject.api.process.ProcessNode;
import static de.fub.mapforgeproject.api.process.ProcessState.ERROR;
import static de.fub.mapforgeproject.api.process.ProcessState.INACTIVE;
import static de.fub.mapforgeproject.api.process.ProcessState.RUNNING;
import de.fub.mapsforge.project.detector.model.AbstractDetectorProcess;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class Task extends AbstractDetectorProcess<InferenceModelResultDataSet, Void> {

    private InferenceModelResultDataSet resultDataSet;

    public Task() {
        super(null);
    }

    public Task(Detector detector) {
        super(detector);
    }

    @Override
    public void setInput(InferenceModelResultDataSet input) {
        this.resultDataSet = input;
    }

    protected InferenceModelResultDataSet getResultDataSet() {
        return resultDataSet;
    }

    @Override
    public Void getResult() {
        return null;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    protected Node createNodeDelegate() {
        return new TaskProcessNode(Task.this);
    }

    @Override
    protected Image getDefaultImage() {
        return IconRegister.findRegisteredIcon("processIconNormal.png");
    }

    private static class TaskProcessNode extends AbstractNode implements PropertyChangeListener {

        private final Task taskProcess;

        public TaskProcessNode(Task taskProcess) {
            super(Children.LEAF, Lookups.fixed(taskProcess));
            this.taskProcess = taskProcess;
            this.taskProcess.addPropertyChangeListener(WeakListeners.propertyChange(TaskProcessNode.this, this.taskProcess));
            setDisplayName(taskProcess.getName());
            setShortDescription(taskProcess.getDescription());
        }

        @Override
        public Image getIcon(int type) {
            Image image = null;
            Image backgroundIcon = null;
            Image overlayIcon = null;
            switch (this.taskProcess.getProcessState()) {
                case ERROR:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconError.png");
                    overlayIcon = IconRegister.findRegisteredIcon("errorHintIcon.png");
                    break;
                case INACTIVE:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconNormal.png");
                    break;
                case RUNNING:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconRun.png");
                    overlayIcon = IconRegister.findRegisteredIcon("playHintIcon.png");
                    break;
                default:
                    throw new AssertionError();
            }

            if (backgroundIcon != null && overlayIcon != null) {
                image = ImageUtilities.mergeImages(backgroundIcon, overlayIcon, 0, 0);
            } else if (backgroundIcon != null) {
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
    }
}
