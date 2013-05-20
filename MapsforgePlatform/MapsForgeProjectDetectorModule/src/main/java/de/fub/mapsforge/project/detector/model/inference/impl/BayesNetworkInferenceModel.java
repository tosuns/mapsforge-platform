/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.impl;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_BayesNetClassifier_Name=Bayes Network InferenceModel",
    "CLT_BayesNetClassifier_Description=A bayes network classifier"
})
@ServiceProvider(service = AbstractInferenceModel.class)
public class BayesNetworkInferenceModel extends AbstractInferenceModel {

    private BayesNet classifier = null;
    private JToolBar toolbar = null;

    public BayesNetworkInferenceModel() {
    }

    @Override
    protected Classifier createClassifier() {
        if (classifier == null) {
            classifier = new BayesNet();
        }
        return classifier;
    }

    private void initToolBar() {
        toolbar.setFloatable(false);
        toolbar.add(new JButton(new ShowGraphAction(BayesNetworkInferenceModel.this)));
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
    public String getName() {
        if (getInferenceModelDescriptor() != null && getInferenceModelDescriptor().getName() != null) {
            return getInferenceModelDescriptor().getName();
        }
        return Bundle.CLT_BayesNetClassifier_Name();
    }

    @Override
    public String getDescription() {
        if (getInferenceModelDescriptor() != null && getInferenceModelDescriptor().getDescription() != null) {
            return getInferenceModelDescriptor().getDescription();
        }
        return Bundle.CLT_BayesNetClassifier_Description();
    }
}
