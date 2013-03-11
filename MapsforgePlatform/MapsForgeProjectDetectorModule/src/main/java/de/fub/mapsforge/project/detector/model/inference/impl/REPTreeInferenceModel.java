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
import weka.classifiers.trees.REPTree;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_REPTreeInferenceModel_Name=REP Tree Inference Model",
    "CLT_REPTreeInferenceModel_Description=Inference Model which uses a REPTree classifiere that builds a decision/regression tree using information gain/variance and prunes it using reduced-error pruning (with backfitting). Only sorts values for numeric attributes once. Missing values are dealt with by splitting the corresponding instances into pieces (i.e. as in C4.5)."
})
@ServiceProvider(service = AbstractInferenceModel.class)
public class REPTreeInferenceModel extends AbstractInferenceModel {

    public REPTreeInferenceModel() {
    }

    public REPTreeInferenceModel(Detector detector) {
        super(detector);
    }

    @Override
    protected Classifier createClassifier() {
        return new REPTree();
    }

    @Override
    public String getName() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_REPTreeInferenceModel_Name();
    }

    @Override
    public String getDescription() {
        if (getInferenceModelDescriptor() != null) {
            return getInferenceModelDescriptor().getDescription();
        }
        return Bundle.CLT_REPTreeInferenceModel_Description();
    }
}
