/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.postprocessors.tasks;

import de.fub.mapsforge.project.detector.model.TrainingsDataProvider;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelInputDataSet;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_DataAnalyzeTask_Name=Analyze Data",
    "CLT_DataAnalyzeTask_Description=A Run time trainings data analyzer task. The propose of this task is to display the statistics of the training data set after it goes through the filters."
})
@ServiceProvider(service = Task.class)
public class DataAnalyzeTask extends Task {

    private final HashMap<String, HashSet<TrackSegment>> data = new HashMap<String, HashSet<TrackSegment>>();

    public DataAnalyzeTask() {
    }

    @Override
    protected void start() {
        // do nothing
        data.clear();
        InferenceModelInputDataSet input = getDetector().getInferenceModel().getInput();
        HashMap<String, HashSet<TrackSegment>> trainingsSet = input.getTrainingsSet();
        data.putAll(trainingsSet);
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public String getName() {
        String name = null;
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            name = getProcessDescriptor().getName();
        } else {
            name = Bundle.CLT_DataAnalyzeTask_Name();
        }
        return name;
    }

    @Override
    public String getDescription() {
        String description = null;
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            description = getProcessDescriptor().getDescription();
        } else {
            description = Bundle.CLT_DataAnalyzeTask_Description();
        }
        return description;
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataAnalyzerTaskNode(this);
    }

    public HashMap<String, HashSet<TrackSegment>> getData() {
        return data;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(DataAnalyzeTask.class.getName());
        descriptor.setName(Bundle.CLT_DataAnalyzeTask_Name());
        descriptor.setDescription(Bundle.CLT_DataAnalyzeTask_Description());
        return descriptor;
    }

    private static class DataAnalyzerTaskNode extends Task.TaskProcessNode {

        private final DataAnalyzeTask task;

        public DataAnalyzerTaskNode(DataAnalyzeTask taskProcess) {
            super(taskProcess);
            this.task = taskProcess;
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{new DisplayDataAnalyzerViewAction(task), new ClearAction("Clear")};
        }

        private class ClearAction extends AbstractAction {

            private static final long serialVersionUID = 1L;

            public ClearAction(String name) {
                super(name);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                task.getData().clear();
            }
        }
    }

    private static class DisplayDataAnalyzerViewAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final DataAnalyzeTask task;

        @NbBundle.Messages({"CLT_DisplayDataAnalyzerView_Action_Name=Show Data Analyzer"})
        public DisplayDataAnalyzerViewAction(DataAnalyzeTask task) {
            super(Bundle.CLT_DisplayDataAnalyzerView_Action_Name());
            this.task = task;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DataAnalyzeTaskTopComponent tc = new DataAnalyzeTaskTopComponent(task, Lookups.fixed(task.getDetector().getDataObject().getNodeDelegate(), new TrainingsDataProviderImpl()));
                    tc.open();
                    tc.requestActive();
                    task.getData().clear();
                }
            });
        }

        class TrainingsDataProviderImpl implements TrainingsDataProvider {

            public TrainingsDataProviderImpl() {
            }

            @Override
            public Map<String, List<TrackSegment>> getData() {
                HashMap<String, List<TrackSegment>> result = new HashMap<String, List<TrackSegment>>();

                for (Entry<String, HashSet<TrackSegment>> entry : task.getData().entrySet()) {
                    result.put(entry.getKey(), new ArrayList<TrackSegment>(entry.getValue()));
                }
                return result;
            }
        }
    }
}
