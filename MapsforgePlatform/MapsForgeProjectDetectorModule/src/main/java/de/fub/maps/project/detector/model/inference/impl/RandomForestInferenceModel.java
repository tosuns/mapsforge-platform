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
