/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.postprocessors.tasks;

import static de.fub.mapforgeproject.api.process.ProcessState.ERROR;
import static de.fub.mapforgeproject.api.process.ProcessState.INACTIVE;
import static de.fub.mapforgeproject.api.process.ProcessState.RUNNING;
import de.fub.mapsforge.project.detector.model.AbstractDetectorProcess;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.node.CustomAbstractnode;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
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

    @NbBundle.Messages({"CLT_Tesk_Parameter=Parameters"})
    protected static class TaskProcessNode extends CustomAbstractnode implements PropertyChangeListener, ChangeListener {

        private final Task taskProcess;
        private Sheet.Set set;
        private ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

        public TaskProcessNode(Task taskProcess) {
            super(Children.LEAF, Lookups.fixed(taskProcess));
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
            List<de.fub.mapsforge.project.detector.model.xmls.Property> propertyList = taskProcess.getProcessDescriptor().getProperties().getPropertyList();
            ProcessProperty property = null;
            for (de.fub.mapsforge.project.detector.model.xmls.Property xmlProperty : propertyList) {
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
