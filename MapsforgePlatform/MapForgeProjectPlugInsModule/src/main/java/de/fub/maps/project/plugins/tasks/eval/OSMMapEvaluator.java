/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.plugins.tasks.eval;

import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.maps.project.openstreetmap.service.MapProvider;
import de.fub.maps.project.plugins.mapmatcher.MapMatcher;
import java.awt.Dialog;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Serdar
 */
public class OSMMapEvaluator {

    private final RoadNetwork roadNetwork;

    public OSMMapEvaluator(RoadNetwork roadNetwork) {
        assert roadNetwork != null;
        this.roadNetwork = roadNetwork;
    }

    public void evaluate() {
        final EvaluationOptionPanel evaluationOptionPanel = new EvaluationOptionPanel();

        DialogDescriptor descriptor = new DialogDescriptor(
                evaluationOptionPanel,
                "Evaluator Option Dialog",
                true,
                evaluationOptionPanel.getButtons(),
                evaluationOptionPanel.getOkButton(),
                DialogDescriptor.RIGHT_ALIGN,
                HelpCtx.DEFAULT_HELP,
                null);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setVisible(true);
        if (descriptor.getValue() == evaluationOptionPanel.getOkButton()) {
            final MapMatcher mapMatcher = evaluationOptionPanel.getMapMatcher();
            final MapProvider mapProvider = evaluationOptionPanel.getMapProvider();

            if (mapMatcher != null && mapProvider != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EvaluatorTopComponent evaluatorTopComponent = new EvaluatorTopComponent(mapMatcher, mapProvider, roadNetwork);
                        evaluatorTopComponent.open();
                        evaluatorTopComponent.requestVisible();
                    }
                });

            } else {
                NotifyDescriptor.Message nd = new NotifyDescriptor.Message("MapMatcher or MapProvider instance are null. This state is not permitted!");
                DialogDisplayer.getDefault().notifyLater(nd);
            }
        }
    }
}
