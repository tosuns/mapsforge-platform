/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference.impl;

import de.fub.maps.project.detector.model.inference.AbstractInferenceModel;
import de.fub.maps.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.maps.project.detector.utils.DetectorUtils;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_RandomForestInferenceModel_Name=Random Forest Inference Model",
    "CLT_RandomForestInferenceModel_Description=Inference Model which uses a RandomForest Classifier"
})
@ServiceProvider(service = AbstractInferenceModel.class)
public class RandomForestInferenceModel extends AbstractInferenceModel {

    private JToolBar toolbar = null;
    private RandomForest classifier = null;

    public RandomForestInferenceModel() {
    }

    private void initToolBar() {
        toolbar.setFloatable(false);
        toolbar.add(new JButton(new ShowGraphAction(RandomForestInferenceModel.this)));
    }

    @Override
    public JToolBar getToolbarRepresenter() {
        if (toolbar == null) {
            toolbar = new JToolBar();
            initToolBar();
        }
        return toolbar;
    }

    @Override
    protected Classifier createClassifier() {
        classifier = new RandomForest();
        return classifier;
    }

    @Override
    public String getName() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_RandomForestInferenceModel_Name();
    }

    @Override
    public String getDescription() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_RandomForestInferenceModel_Description();
    }

    @Override
    protected InferenceModelDescriptor createDefaultDescriptor() {
        InferenceModelDescriptor xmlDescriptor = null;
        try {
            xmlDescriptor = DetectorUtils.getXmlDescriptor(InferenceModelDescriptor.class, getClass());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return xmlDescriptor;
    }
}
