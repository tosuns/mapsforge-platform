/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    private JToolBar toolbar = null;
    private REPTree repTree = null;

    public REPTreeInferenceModel() {
    }

    private void initToolBar() {
        toolbar.setFloatable(false);
        toolbar.add(new JButton(new ShowGraphAction(REPTreeInferenceModel.this)));
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
        repTree = new REPTree();
        return repTree;
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
