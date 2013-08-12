/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.accuracy;

import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.tasks.Task;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_AccuracyAnalyzer_Name=Accuracy Analyzer",
    "CLT-AccuracyAnalyzer_Description=Computes the accuracy for each Transport mode,. This task works only with the SpecialInferenceDataProcessHandler."
})
@ServiceProvider(service = Task.class)
public class AccuracyAnalyzer extends Task {

    private final TreeSet<AnalayzerDatasetItem> resultSet = new TreeSet<AnalayzerDatasetItem>();
    private AccuracyResultTopComponent accuracyResultTopComponent;

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor(getName(), getDescription(), AccuracyAnalyzer.class.getName());
        return descriptor;
    }

    @Override
    protected void start() {
        if (getDetector() != null && getDetector().getInferenceModel() != null) {
            HashMap<String, HashSet<TrackSegment>> trainingsSet = getDetector().getInferenceModel().getInput().getTrainingsSet();
            InferenceModelResultDataSet result = getDetector().getInferenceModel().getResult();

            if (result != null && trainingsSet != null) {
                resultSet.clear();

                for (Entry<String, HashSet<TrackSegment>> entry : trainingsSet.entrySet()) {

                    if (result.containsKey(entry.getKey())) {
                        HashSet<TrackSegment> list = result.get(entry.getKey());
                        int correctClassifiedSegmentCount = 0;
                        for (TrackSegment trackSegment : list) {
                            if (entry.getKey().equals(trackSegment.getLabel())) {
                                correctClassifiedSegmentCount++;
                            }
                        }

                        if (correctClassifiedSegmentCount > 0) {
                            resultSet.add(new AnalayzerDatasetItem(entry.getKey(),
                                    entry.getValue().size(),
                                    correctClassifiedSegmentCount,
                                    list.size()));
                        }
                    } else {
                        resultSet.add(new AnalayzerDatasetItem(entry.getKey(), 0, 0, 0));
                    }
                }
                showResult();
            }
        }
    }

    private AccuracyResultTopComponent getTopComponent() {
        if (this.accuracyResultTopComponent == null) {
            accuracyResultTopComponent = new AccuracyResultTopComponent();
            accuracyResultTopComponent.setName(MessageFormat.format("{0} [{1}]", accuracyResultTopComponent.getName(), getDetector().getName()));

            Mode mode = WindowManager.getDefault().findMode("properties");
            if (mode != null) {
                mode.dockInto(accuracyResultTopComponent);
            }
        }
        return this.accuracyResultTopComponent;
    }

    private void showResult() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                getTopComponent().setResult(resultSet);
                if (!getTopComponent().isOpened()) {
                    getTopComponent().open();
                }
                getTopComponent().requestActive();
            }
        });
    }

    @Override
    protected Node createNodeDelegate() {
        return new AccuracyAnalyzerNode(AccuracyAnalyzer.this);
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public String getName() {
        return Bundle.CLT_AccuracyAnalyzer_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_AccuracyAnalyzer_Description();
    }

    private static class DisplayComponentAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final AccuracyAnalyzer task;

        public DisplayComponentAction(AccuracyAnalyzer task) {
            this.task = task;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            task.showResult();
        }
    }

    private class AccuracyAnalyzerNode extends TaskProcessNode {

        private final AccuracyAnalyzer task;

        public AccuracyAnalyzerNode(AccuracyAnalyzer taskProcess) {
            super(taskProcess);
            this.task = taskProcess;
        }

        @Override
        public Action[] getActions(boolean context) {
            Action[] actions = new Action[]{new DisplayComponentAction(task)};
            return actions;
        }
    }

    static class AnalayzerDatasetItem implements Comparable<AnalayzerDatasetItem> {

        private final String transportMode;
        private final double totalNumberOfInstances;
        private final double correctClassifiedNumberInstances;
        private final double classifiedAsTransportModeInstances;

        public AnalayzerDatasetItem(String transportmode,
                double totalNumberOfInstances,
                double correctClassifiedNumberInstances,
                double classifiedAsTransportModeInstances) {
            this.transportMode = transportmode;
            this.totalNumberOfInstances = totalNumberOfInstances;
            this.correctClassifiedNumberInstances = correctClassifiedNumberInstances;
            this.classifiedAsTransportModeInstances = classifiedAsTransportModeInstances;
        }

        public String getTransportMode() {
            return transportMode;
        }

        public double getTotalNumberOfInstances() {
            return totalNumberOfInstances;
        }

        public double getCorrectClassifiedNumberInstances() {
            return correctClassifiedNumberInstances;
        }

        public double getClassifiedAsTransportModeInstances() {
            return classifiedAsTransportModeInstances;
        }

        public double getRecallAccuracy() {
            return getTotalNumberOfInstances() == 0 ? 0 : getCorrectClassifiedNumberInstances() / getTotalNumberOfInstances();
        }

        public double getPrecisionAccuracy() {
            return getClassifiedAsTransportModeInstances() == 0 ? 0 : getCorrectClassifiedNumberInstances() / getClassifiedAsTransportModeInstances();
        }

        @Override
        public int compareTo(AnalayzerDatasetItem o) {
            return getTransportMode().compareToIgnoreCase(o.getTransportMode());
        }
    }
}
