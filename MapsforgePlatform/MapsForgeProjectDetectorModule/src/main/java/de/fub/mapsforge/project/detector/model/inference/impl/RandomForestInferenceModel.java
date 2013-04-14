/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.impl;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import javax.swing.JButton;
import javax.swing.JToolBar;
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

    public RandomForestInferenceModel(Detector detector) {
        super(detector);
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
}
