/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.impl;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
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

    public RandomForestInferenceModel() {
    }

    public RandomForestInferenceModel(Detector detector) {
        super(detector);
    }

    @Override
    protected Classifier createClassifier() {
        return new RandomForest();
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
