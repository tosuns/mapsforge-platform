/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.inference;

import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapsforge.project.detector.factories.inference.InferenceNodeChildFactory;
import de.fub.mapsforge.project.detector.factories.nodes.ProcessProperty;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Section;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.node.CustomAbstractnode;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_No_InferenceModel_Name=<No Inference Model>",
    "CLT_No_InferenceModel_Description=Without an inference model the classifcation process can not be run."
})
public class InferenceModelNode extends CustomAbstractnode implements ChangeListener, ProcessPipeline.ProcessListener, PropertyChangeListener {

    private static final String ICON_NAME = "inferenceModelIcon.png";
    public static final String ACTION_PATH = "MapsForge/Detector/inferenceModel/Actions";
    private AbstractInferenceModel inferenceModel;
    private InstanceContent content = null;
    private Detector detector;
    private ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private Sheet sheet;

    public InferenceModelNode(Detector detector) {
        this(detector, new InstanceContent());
    }

    private InferenceModelNode(Detector detector, InstanceContent content) {
        super(Children.create(new InferenceNodeChildFactory(detector), true), new AbstractLookup(content));
        if (detector != null) {
            this.content = content;
            this.detector = detector;
            updateNode();
        }
    }

    /**
     * Constructor for the stand alone case, when the inference model does not
     * have a Detector instance as parent.
     *
     * @param inferenceModel
     */
    public InferenceModelNode(AbstractInferenceModel inferenceModel) {
        this(inferenceModel, new InstanceContent());
    }

    private InferenceModelNode(AbstractInferenceModel inferenceModel, InstanceContent content) {
        super(Children.LEAF, new AbstractLookup(content));
        assert inferenceModel != null;
        this.content = content;
        this.inferenceModel = inferenceModel;
        this.content.add(inferenceModel);
        updateNode();
    }

    private void updateNode() {
        if (detector != null) {

            AbstractInferenceModel oldInferenceModel = getLookup().lookup(AbstractInferenceModel.class);
            if (oldInferenceModel != null) {
                content.remove(oldInferenceModel);
                oldInferenceModel.removeProcessListener(InferenceModelNode.this);
                oldInferenceModel.removePropertyChangeListener(InferenceModelNode.this);
            }
            inferenceModel = detector.getInferenceModel();
            if (inferenceModel != null) {
                content.add(inferenceModel);
                setDisplayName(inferenceModel.getName());
                setShortDescription(inferenceModel.getDescription());
                inferenceModel.addProcessListener(InferenceModelNode.this);
                inferenceModel.addPropertyChangeListener(InferenceModelNode.this);
                updateSheet();
            }
        }
        fireIconChange();
    }

    @Override
    protected Sheet createSheet() {
        if (sheet == null) {
            sheet = Sheet.createDefault();
        }
        updateSheet();
        return sheet;
    }

    private ModelSynchronizer.ModelSynchronizerClient getModelSynchronizerClient() {
        if (modelSynchronizerClient == null && detector != null) {
            modelSynchronizerClient = detector.create(InferenceModelNode.this);
        }
        return modelSynchronizerClient;
    }

    @SuppressWarnings("unchecked")
    private void updateSheet() {
        if (sheet != null && inferenceModel != null && inferenceModel.getInferenceModelDescriptor() != null) {
            InferenceModelDescriptor inferenceModelDescriptor = inferenceModel.getInferenceModelDescriptor();
            Sheet.Set set = Sheet.createPropertiesSet();
            sheet.put(set);
            for (Section section : inferenceModelDescriptor.getPropertySection().getSectionList()) {
                if (AbstractInferenceModel.OPTIONS_PROPERTY_SECTION.equals(section.getId())) {
                    for (de.fub.mapsforge.project.detector.model.xmls.Property property : section.getPropertyList()) {
                        if (getModelSynchronizerClient() != null) {
                            set.put(new ProcessProperty(getModelSynchronizerClient(), property));
                        } else {
                            set.put(new ProcessProperty(getModelSynchronizerClient(), property));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        if (inferenceModel != null) {
            return inferenceModel.getName();
        }
        return Bundle.CLT_No_InferenceModel_Name();
    }

    @Override
    public String getShortDescription() {
        if (inferenceModel != null) {
            return inferenceModel.getDescription();
        }
        return Bundle.CLT_No_InferenceModel_Description();
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

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath(ACTION_PATH);
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public Image getIcon(int type) {
        Image image = null;
        if (inferenceModel != null) {
            image = inferenceModel.getIcon();
        } else {
            image = IconRegister.findRegisteredIcon(ICON_NAME);
            image = ImageUtilities.createDisabledImage(image);
//            image = ImageUtilities.mergeImages(image, IconRegister.findRegisteredIcon("errorHintIcon.png"), 0, 0);
        }
        return image != null ? image : super.getIcon(type);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateNode();
    }
}
