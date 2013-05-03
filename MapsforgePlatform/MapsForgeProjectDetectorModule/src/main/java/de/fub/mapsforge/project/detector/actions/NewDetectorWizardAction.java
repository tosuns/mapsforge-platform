/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.model.AbstractDetectorProcess;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.tasks.Task;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.DataSet;
import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.TransportMode;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.mapsforge.project.detector.wizards.detector.CommonDetectorInformationWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.DetectorTemplateWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.InferenceDataSetSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.InferenceModelFeatureSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.InferenceModelSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.PostprocessorSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.PreprocessorSelectionWizardPanel;
import de.fub.mapsforge.project.detector.wizards.detector.TrainingSetSelectionWizardPanel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

@ActionID(category = "Detector", id = "de.fub.mapsforge.project.detector.actions.NewDetectorWizardAction")
@ActionRegistration(displayName = "#CLT_NewDetectorWizardAction_Name")
@ActionReference(path = "Projects/org-mapsforge-project/Detector/Actions", position = 100)
@NbBundle.Messages({
    "CLT_NewDetectorWizardAction_Name=New Detector...",
    "CLT_New_Detector_Wizard_Title=New Detector Wizard"
})
public final class NewDetectorWizardAction implements ActionListener, WizardDescriptor.Iterator<WizardDescriptor>, PropertyChangeListener {

    public static final String PROP_NAME_DATAOBJECT = "detector.wizard.dataObject";
    public static final String PROP_NAME_CREATE_VIA_TEMPLATE = "create.via.template";
    private WizardDescriptor wiz;
    private int index;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    private final ChangeSupport cs = new ChangeSupport(this);
    private List<WizardDescriptor.Panel<WizardDescriptor>> viaTemplatePanels = null;
    private List<WizardDescriptor.Panel<WizardDescriptor>> withoutTemplatePanels = null;
    private final DataObject context;

    public NewDetectorWizardAction(DataObject context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        if (context != null) {
            FileUtil.refreshFor(FileUtil.toFile(context.getPrimaryFile()));
            panels = null;
            index = 0;
            wiz = new WizardDescriptor(NewDetectorWizardAction.this);
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle(Bundle.CLT_New_Detector_Wizard_Title());
            wiz.putProperty(PROP_NAME_DATAOBJECT, context);
            wiz.addPropertyChangeListener(WeakListeners.propertyChange(NewDetectorWizardAction.this, wiz));
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                if (panels == withoutTemplatePanels) {
                    handleSimpleDetectorCreation();
                } else if (panels == viaTemplatePanels) {
                    handleDetectorCreationViaTemplate();
                }
                FileUtil.refreshFor(FileUtil.toFile(context.getPrimaryFile()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleSimpleDetectorCreation() {
        String detectorName = null;
        String detectorDescription = null;
        AbstractInferenceModel abstractInferenceModel = null;
        List<FeatureProcess> selectedFeatureList = null;
        List<FilterProcess> selectedFilterList = null;
        List<Task> selectedTaskList = null;
        Map<String, List<Node>> trainingsMap = null;
        List<Node> inferenceDataSet = null;
        for (int position = 0; position < withoutTemplatePanels.size(); position++) {
            switch (position) {
                case 0:
                    detectorName = (String) wiz.getProperty(CommonDetectorInformationWizardPanel.PROP_NAME_DETECTOR_NAME);
                    detectorDescription = (String) wiz.getProperty(CommonDetectorInformationWizardPanel.PROP_NAME_DETECTOR_DESCRIPTION);
                    break;
                case 1:
                    abstractInferenceModel = (AbstractInferenceModel) wiz.getProperty(InferenceModelSelectionWizardPanel.PROP_NAME_INFERENCEMODEL);
                    break;
                case 2:
                    selectedFeatureList = (List<FeatureProcess>) wiz.getProperty(InferenceModelFeatureSelectionWizardPanel.PROP_NAME_FEATURES);
                    break;
                case 3:
                    selectedFilterList = (List<FilterProcess>) wiz.getProperty(PreprocessorSelectionWizardPanel.PROP_NAME_SELECTED_PREPROCESSORS);
                    break;
                case 4:
                    selectedTaskList = (List<Task>) wiz.getProperty(PostprocessorSelectionWizardPanel.PROP_NAME_SELECTED_POSTPROCESSORS);
                    break;
                case 5:
                    trainingsMap = (Map<String, List<Node>>) wiz.getProperty(TrainingSetSelectionWizardPanel.PROP_NAME_TRAININGS_SET);
                    break;
                case 6:
                    inferenceDataSet = (List<Node>) wiz.getProperty(InferenceDataSetSelectionWizardPanel.PROP_NAME_INFERENCE_DATA);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        if (detectorName != null && abstractInferenceModel != null) {
            DetectorDescriptor detectorDescriptor = new DetectorDescriptor();
            detectorDescriptor.setName(detectorName);
            detectorDescriptor.setDescription(detectorDescription);
            InferenceModelDescriptor inferenceModelDescriptor = abstractInferenceModel.getInferenceModelDescriptor();
            if (inferenceModelDescriptor != null) {
                try {
                    detectorDescriptor.setInferenceModel(inferenceModelDescriptor);
                    inferenceModelDescriptor.getFeatures().getFeatureList().clear();

                    addProcessDescriptorList(inferenceModelDescriptor.getFeatures().getFeatureList(), selectedFeatureList);

                    addProcessDescriptorList(detectorDescriptor.getPreprocessors().getPreprocessorList(), selectedFilterList);

                    addProcessDescriptorList(detectorDescriptor.getPostprocessors().getPostprocessorList(), selectedTaskList);

                    addTrainingsSet(detectorDescriptor, trainingsMap);

                    addInferenceDataset(detectorDescriptor, inferenceDataSet);
                    int lastIndexOf = detectorName.lastIndexOf(".");
                    detectorName = lastIndexOf > -1 ? detectorName.substring(0, lastIndexOf) : detectorName;
                    FileObject destFileObject = context.getPrimaryFile().createData(MessageFormat.format("{0}.dec", detectorName));
                    DetectorUtils.saveDetector(destFileObject, detectorDescriptor);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private void addProcessDescriptorList(List<ProcessDescriptor> detectorDescriptor, List< ? extends AbstractDetectorProcess<?, ?>> list) {
        ProcessDescriptor processDescriptor = null;
        if (list != null) {
            for (AbstractDetectorProcess<?, ?> process : list) {
                processDescriptor = process.getProcessDescriptor();
                if (processDescriptor != null) {
                    detectorDescriptor.add(processDescriptor);
                }
            }
        }
    }

    private void addTrainingsSet(DetectorDescriptor detectorDescriptor, Map<String, List<Node>> trainingsMap) {
        FileObject datasourceFileObject = DetectorUtils.getDatasourceFileObject();
        if (datasourceFileObject != null) {
            if (trainingsMap != null) {
                List<TransportMode> transportModeList = detectorDescriptor.getDatasets().getTrainingSet().getTransportModeList();
                for (Entry<String, List<Node>> entry : trainingsMap.entrySet()) {
                    TransportMode transportMode = new TransportMode(entry.getKey());
                    transportModeList.add(transportMode);
                    for (Node node : entry.getValue()) {
                        DataObject dataObject = node.getLookup().lookup(DataObject.class);
                        if (dataObject != null) {
                            transportMode.getDataset().add(
                                    new DataSet(
                                    MessageFormat.format("{0}/{1}", datasourceFileObject.getName(),
                                    FileUtil.getRelativePath(
                                    datasourceFileObject,
                                    dataObject.getPrimaryFile()))));
                        }
                    }
                }
            }
        }
    }

    private void addInferenceDataset(DetectorDescriptor detectorDescriptor, List<Node> inferenceDataSet) {
        FileObject datasourceFileObject = DetectorUtils.getDatasourceFileObject();
        if (datasourceFileObject != null) {


            if (inferenceDataSet != null) {
                List<DataSet> datasetList = detectorDescriptor.getDatasets().getInferenceSet().getDatasetList();
                for (Node node : inferenceDataSet) {
                    DataObject dataObject = node.getLookup().lookup(DataObject.class);
                    if (dataObject != null) {
                        datasetList.add(new DataSet(
                                MessageFormat.format("{0}/{1}", datasourceFileObject.getName(),
                                FileUtil.getRelativePath(
                                datasourceFileObject,
                                dataObject.getPrimaryFile()))));
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "empty-statement"})
    private void handleDetectorCreationViaTemplate() {
        String detectorName = null;
        String detectorDescription = null;
        Detector detectorTemplate = null;
        Map<String, List<Node>> trainingsMap = null;
        List<Node> inferenceDataSet = null;
        for (int position = 0; position < viaTemplatePanels.size(); position++) {
            switch (position) {
                case 0:
                    detectorName = (String) wiz.getProperty(CommonDetectorInformationWizardPanel.PROP_NAME_DETECTOR_NAME);
                    detectorDescription = (String) wiz.getProperty(CommonDetectorInformationWizardPanel.PROP_NAME_DETECTOR_DESCRIPTION);
                    break;
                case 1:
                    detectorTemplate = (Detector) wiz.getProperty(DetectorTemplateWizardPanel.PROP_NAME_TEMPLATE_INSTANCE);
                    break;
                case 2:
                    trainingsMap = (Map<String, List<Node>>) wiz.getProperty(TrainingSetSelectionWizardPanel.PROP_NAME_TRAININGS_SET);
                    break;
                case 3:
                    inferenceDataSet = (List<Node>) wiz.getProperty(InferenceDataSetSelectionWizardPanel.PROP_NAME_INFERENCE_DATA);
                    break;
                default:
                    throw new AssertionError();
            }
        }


        if (detectorName != null && detectorTemplate != null) {
            try {
                Detector detector = DetectorUtils.copyInstance(detectorTemplate);
                DetectorDescriptor detectorDescriptor = detector.getDetectorDescriptor();
                detectorDescriptor.setName(detectorName);

                if (detectorDescription != null) {
                    detectorDescriptor.setDescription(detectorDescription);
                }

                // add trainings map values to the descriptor
                addTrainingsSet(detectorDescriptor, trainingsMap);

                // add inference set values to the descriptor
                addInferenceDataset(detectorDescriptor, inferenceDataSet);

                int lastIndexOf = detectorName.lastIndexOf(".");
                detectorName = lastIndexOf > -1 ? detectorName.substring(0, lastIndexOf) : detectorName;
                FileObject destFileObject = context.getPrimaryFile().createData(MessageFormat.format("{0}.dec", detectorName));
                DetectorUtils.saveDetector(destFileObject, detectorDescriptor);
            } catch (DetectorUtils.DetectorCopyException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void configurePanels(List<WizardDescriptor.Panel<WizardDescriptor>> panels) {
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) {
                // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        wiz.putProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
    }

    @SuppressWarnings("unchecked")
    private List<WizardDescriptor.Panel<WizardDescriptor>> createPanels() {
        if (panels == null) {
            WizardDescriptor.Panel<WizardDescriptor> commonDetectorInformationWizardPanel = new CommonDetectorInformationWizardPanel();
            WizardDescriptor.Panel<WizardDescriptor> inferenceModelSelectionWizardPanel = new InferenceModelSelectionWizardPanel();
            WizardDescriptor.Panel<WizardDescriptor> inferenceModelFeatureSelectionWizardPanel = new InferenceModelFeatureSelectionWizardPanel();
            WizardDescriptor.Panel<WizardDescriptor> preprocessorSelectionWizardPanel = new PreprocessorSelectionWizardPanel();
            WizardDescriptor.Panel<WizardDescriptor> postprocessorSelectionWizardPanel = new PostprocessorSelectionWizardPanel();
            WizardDescriptor.Panel<WizardDescriptor> trainingSetSelectionWizardPanel = new TrainingSetSelectionWizardPanel();
            WizardDescriptor.Panel<WizardDescriptor> inferenceDataSetSelectionWizardPanel = new InferenceDataSetSelectionWizardPanel();
            WizardDescriptor.Panel<WizardDescriptor> detectorTemplateWizardPanel = new DetectorTemplateWizardPanel();

            withoutTemplatePanels = Arrays.asList(
                    commonDetectorInformationWizardPanel,
                    inferenceModelSelectionWizardPanel,
                    inferenceModelFeatureSelectionWizardPanel,
                    preprocessorSelectionWizardPanel,
                    postprocessorSelectionWizardPanel,
                    trainingSetSelectionWizardPanel,
                    inferenceDataSetSelectionWizardPanel);



            viaTemplatePanels = Arrays.asList(
                    commonDetectorInformationWizardPanel,
                    detectorTemplateWizardPanel,
                    trainingSetSelectionWizardPanel,
                    inferenceDataSetSelectionWizardPanel);

            // default panel array
            panels = withoutTemplatePanels;
            configurePanels(panels);
        }
        return panels;
    }

    @SuppressWarnings("unchecked")
    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        List<WizardDescriptor.Panel<WizardDescriptor>> currentPanels = createPanels();
        if (index == 0) {
            if (wiz.getProperty(PROP_NAME_CREATE_VIA_TEMPLATE) instanceof Boolean
                    && (Boolean) wiz.getProperty(PROP_NAME_CREATE_VIA_TEMPLATE)) {
                if (currentPanels != viaTemplatePanels) {
                    panels = viaTemplatePanels;
                    currentPanels = viaTemplatePanels;
                    configurePanels(currentPanels);
                }
            } else {
                if (currentPanels != withoutTemplatePanels) {
                    panels = withoutTemplatePanels;
                    currentPanels = withoutTemplatePanels;
                    configurePanels(currentPanels);
                }
            }
        }
        return currentPanels;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels().get(index);
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().size();
    }

    @Override
    public boolean hasNext() {

        return index < getPanels().size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed
    @Override
    public void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROP_NAME_CREATE_VIA_TEMPLATE.equals(evt.getPropertyName())) {
            cs.fireChange();
        }
    }
}
