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
import weka.classifiers.trees.J48;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_J48InferenceModel_Name=J48 Inference Model",
    "CLT_J48InferenceModel_Description=Inference Model which uses a J48 classifier that generates an unpruned or a pruned C4.5 decision tree."
})
@ServiceProvider(service = AbstractInferenceModel.class)
public class J48InferenceModel extends AbstractInferenceModel {

    public J48InferenceModel() {
    }

    public J48InferenceModel(Detector detector) {
        super(detector);
    }

    @Override
    protected Classifier createClassifier() {
        return new J48();
    }

    @Override
    public String getName() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_J48InferenceModel_Name();
    }

    @Override
    public String getDescription() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getDescription();
        }
        return Bundle.CLT_J48InferenceModel_Description();
    }
}
